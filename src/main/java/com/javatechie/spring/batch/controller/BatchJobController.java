package com.javatechie.spring.batch.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.javatechie.spring.batch.entity.Customer;
import com.javatechie.spring.batch.repository.CustomerRepository;

@Controller
@RequestMapping("/api")
public class BatchJobController {

    public static final String TEMP_STORAGE_PATH = "/Users/javatechie/Desktop/temp/";
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private CustomerRepository repository;
    @Autowired
    private JobRepository jobRepository;

    private final String TEMP_STORAGE = "/Users/javatechie/Desktop/batch-files/";
    
    @Value("${file.upload-dir}")
    String storageLocation;
    
    @GetMapping("/importData")
    public String getimportFilePage() {
    	return "import_data";
    }
    
    @PostMapping("/importData")
    public String startJob(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) throws IOException {
    	DateTimeFormatter format = DateTimeFormatter.ISO_DATE;
    	String folderName = LocalDate.now().format(format);
    	System.out.println(folderName);
    	System.out.println(file1.getOriginalFilename());
    	System.out.println(file2.getOriginalFilename());
    	Files.createDirectories(Paths.get(storageLocation, folderName));    	
    	Files.copy(file1.getInputStream(), Paths.get(storageLocation, folderName, "file1.csv"), StandardCopyOption.REPLACE_EXISTING);
    	Files.copy(file2.getInputStream(), Paths.get(storageLocation, folderName, "file2.csv"), StandardCopyOption.REPLACE_EXISTING);
    	return "redirect:/api/importData";
    }
    
    @PostMapping("/import-data")
    public void startBatch(@RequestParam("file") MultipartFile multipartFile) {


        // file  -> path we don't know
        //copy the file to some storage in your VM : get the file path
        //copy the file to DB : get the file path

        try {
            String originalFileName = multipartFile.getOriginalFilename();
            File fileToImport = new File(TEMP_STORAGE + originalFileName);
            multipartFile.transferTo(fileToImport);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", TEMP_STORAGE + originalFileName)
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);

//            if(execution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED)){
//                //delete the file from the TEMP_STORAGE
//                Files.deleteIfExists(Paths.get(TEMP_STORAGE + originalFileName));
//            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | IOException e) {

            e.printStackTrace();
        }
    }

    @GetMapping("/customers")
    public List<Customer> getAll() {
        return repository.findAll();
    }
}
