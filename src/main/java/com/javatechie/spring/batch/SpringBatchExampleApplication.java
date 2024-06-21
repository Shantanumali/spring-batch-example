package com.javatechie.spring.batch;

import java.util.Properties;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchExampleApplication.class, args);
	}
}
