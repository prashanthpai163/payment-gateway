package com.paymentGateway.abstractGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication
@ComponentScan("com.paymentGateway.abstractGateway")
public class App 
{
    public static void main(String[] args )
    {
    	SpringApplication.run(App.class, args);
    }
}
