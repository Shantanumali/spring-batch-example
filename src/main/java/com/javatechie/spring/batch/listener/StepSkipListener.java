package com.javatechie.spring.batch.listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StepSkipListener implements SkipListener<Object, Object> {
    
    //@Value("${file.upload-dir}")
    String storageLoacation = "C:/Work Documents/tkdl";

    @OnSkipInRead
    public void onSkipInRead(Throwable t) {       
    	if(!Files.exists(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-read-skipped.txt"))) {
    		try {
    			Files.createDirectories(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors"));
				Files.createFile(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-read-skipped.txt"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		if(t instanceof RuntimeException) {
			writeFile(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-read-skipped.txt").toFile(), 
					((FlatFileParseException) t).getInput());
		}
    }

    @OnSkipInWrite
    public void onSkipInWrite(Object item, Throwable t) {
    	if(!Files.exists(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-write-skipped.txt"))) {
    		try {
    			Files.createDirectories(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors"));
				Files.createFile(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-write-skipped.txt"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	try {
    	writeFile(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-write-skipped.txt").toFile(), 
				item.toString());
    	}catch(RuntimeException e) {
    		writeFile(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-write-skipped.txt").toFile(), 
    				t.getMessage());
    	}
    	}

    @OnSkipInProcess
    public void onSkipInProcess(Object item, Throwable t) {
    	if(!Files.exists(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-process-skipped.txt"))) {
    		try {
    			Files.createDirectories(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors"));
				Files.createFile(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-process-skipped.txt"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	writeFile(Paths.get(storageLoacation, LocalDate.now().format(DateTimeFormatter.ISO_DATE), "errors", "customer-process-skipped.txt").toFile(), 
				item.toString());
    }

    private void writeFile(File file, String record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(record);
                writer.newLine();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
