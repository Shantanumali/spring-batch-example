package com.javatechie.spring.batch.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
	
	@Autowired
	JavaMailSender javaMailSender;
	
	public void sendRejectedRecordsEmail(Stream<Path> fileListStream) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("moriho5892@exeneli.com");
            helper.setSubject("Rejected Records from Batch Jobs");
            helper.setText("The Import Job Completed");
            fileListStream.forEach(e->{
				try {
					helper.addAttachment(e.getFileName().toString(), e.toFile());
				} catch (MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle messaging exception
        }
    }

	public void sendJobErrorStatusMail(BatchStatus status) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("moriho5892@exeneli.com");
            helper.setSubject("Rejected Records from Batch Jobs");
            helper.setText("The Import Job Failed With Status: "+status);
            
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle messaging exception
        }
		
	}
}
