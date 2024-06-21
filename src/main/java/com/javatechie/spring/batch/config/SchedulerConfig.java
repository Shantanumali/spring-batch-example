package com.javatechie.spring.batch.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.javatechie.spring.batch.repository.CustomerRepository;

@EnableScheduling
@Configuration
public class SchedulerConfig {

	@Value("${file.upload-dir}")
	String storageLocation;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@Autowired
	private CustomerRepository repository;

	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private EmailService emailService;

	@Scheduled(cron = " 0 */1 * * * *")
	public void scheduleJob() throws IOException {

		// Check if files present
		String folderName = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
		if(Files.exists(Paths.get(storageLocation, folderName))) {
		long count = Files.list(Paths.get(storageLocation, folderName)).count();
		if (count == 2) {
			if (Files.exists(Paths.get(storageLocation, folderName, "file1.csv"))
					&& Files.exists(Paths.get(storageLocation, folderName, "file2.csv"))) {
				// StartJob
				try {
					JobParameters jobParameters = new JobParametersBuilder()
							.addString("file1",
									Paths.get(storageLocation, folderName, "file1.csv").toAbsolutePath().toString())
							.addString("file2",
									Paths.get(storageLocation, folderName, "file2.csv").toAbsolutePath().toString())
							.addLong("startAt", System.currentTimeMillis()).toJobParameters();

					JobExecution execution = jobLauncher.run(job, jobParameters);
					
					if(execution.getStatus().equals(BatchStatus.COMPLETED)) {
						// check error folder consists file then send mail
						if(Files.exists(Paths.get(storageLocation, folderName, "errors"))){
							//emailService.sendRejectedRecordsEmail(Files.list(Paths.get(storageLocation, folderName, "errors")));
						}
					}else {
						//send mail with job status
						//emailService.sendJobErrorStatusMail(execution.getStatus());
					}

				} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
						| JobParametersInvalidException e) {
					e.printStackTrace();
					// Send mail that job failed
					//emailService.sendJobErrorStatusMail(BatchStatus.FAILED);
				}
			}
		}
		}
	}

}
