package com.javatechie.spring.batch.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.javatechie.spring.batch.entity.Customer;
import com.javatechie.spring.batch.entity.Customer2;
import com.javatechie.spring.batch.listener.StepSkipListener;
import com.javatechie.spring.batch.listener.StepSkipListener2;
import com.javatechie.spring.batch.repository.CustomerRepository;
import com.javatechie.spring.batch.repository.CustomerRepository2;

@Configuration
@EnableBatchProcessing
//@AllArgsConstructor
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerItemWriter customerItemWriter;
    @Autowired
    private CustomerRepository2 customerRepository2;
    @Autowired
    private CustomerItemWriter2 customerItemWriter2;

    @Bean("itemReader1")
    @StepScope
    public FlatFileItemReader<Customer> itemReader(@Value("#{jobParameters[file1]}") String file1Path) {
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(new File(file1Path)));
        flatFileItemReader.setName("CSV-Reader1");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        
        return flatFileItemReader;
    }
    
    @Bean("itemReader2")
    @StepScope
    public FlatFileItemReader<Customer2> itemReader2(@Value("#{jobParameters[file2]}") String file2Path) {
        FlatFileItemReader<Customer2> flatFileItemReader2 = new FlatFileItemReader<>();
    flatFileItemReader2.setResource(new FileSystemResource(new File(file2Path)));
    flatFileItemReader2.setName("CSV-Reader2");
    flatFileItemReader2.setLinesToSkip(1);
    flatFileItemReader2.setLineMapper(lineMapper2());

    return flatFileItemReader2;
}
    
    private LineMapper<Customer2> lineMapper2() {
        DefaultLineMapper<Customer2> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "age");
        //lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer2> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer2.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "age");
        //lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean("CustomerProcessor")
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }
    
    @Bean("CustomerProcessor2")
    public CustomerProcessor2 processor2() {
        return new CustomerProcessor2();
    }
/*
    @Bean("RepositoryItemWriter")
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }
    
    @Bean("RepositoryItemWriter2")
    public RepositoryItemWriter<Customer2> writer2() {
        RepositoryItemWriter<Customer2> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository2);
        writer.setMethodName("save");
        return writer;
    }
    */

    @Bean("step1")
    public Step step1(@Qualifier("itemReader1")FlatFileItemReader<Customer> itemReader) {
        return stepBuilderFactory.get("slaveStep1").<Customer, Customer>chunk(10)
                .reader(itemReader)
                .processor(processor())
                .writer(customerItemWriter)
                .faultTolerant()
                .listener(new StepSkipListener())
                .skipPolicy(skipPolicy())
                //.taskExecutor(taskExecutor())
                .build();
    }
    
    @Bean("step2")
    public Step step2(@Qualifier("itemReader2")FlatFileItemReader<Customer2> itemReader2) {
        return stepBuilderFactory.get("slaveStep2").<Customer2, Customer2>chunk(10)
                .reader(itemReader2)
                .processor(processor2())
                .writer(customerItemWriter2)
                .faultTolerant()
                .listener(new StepSkipListener2())
                .skipPolicy(skipPolicy())
                //.taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public Job runJob(@Qualifier("step1")Step step1, @Qualifier("step2")Step step2) {
        return jobBuilderFactory.get("importCustomer").start(step1).next(step2).build();
    }


    @Bean
    public SkipPolicy skipPolicy() {
        return new ExceptionSkipPolicy();
    }

    /*
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }
*/

}
