package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Customer2;

import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor2 implements ItemProcessor<Customer2, Customer2> {
    @Override
    public Customer2 process(Customer2 customer) {
        int age = Integer.parseInt(customer.getAge());
        if (age >= 18) {
            return customer;
        }
        return null;
    }
}
