package com.fyp.handsome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fyp.handsome.mapper")
public class HandsomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HandsomeApplication.class, args);
	}

}
