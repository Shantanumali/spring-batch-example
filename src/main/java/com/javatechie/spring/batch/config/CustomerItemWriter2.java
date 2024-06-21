package com.javatechie.spring.batch.config;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javatechie.spring.batch.entity.Customer2;
import com.javatechie.spring.batch.repository.CustomerRepository2;

@Component
public class CustomerItemWriter2 implements ItemWriter<Customer2> {

    @Autowired
    private CustomerRepository2 repository;

    @Override
    public void write(List<? extends Customer2> list) throws Exception {
        System.out.println("Writer Thread "+Thread.currentThread().getName());
        repository.saveAll(list);
    }
}
