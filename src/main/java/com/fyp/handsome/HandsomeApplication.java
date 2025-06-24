package com.fyp.handsome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.fyp.handsome.mapper")
@EnableAsync
public class HandsomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HandsomeApplication.class, args);
	}

}
