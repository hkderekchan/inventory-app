package com.mysite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class InventoryApplication {

	public static void main(final String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

}
