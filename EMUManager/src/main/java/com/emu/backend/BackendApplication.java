package com.emu.backend;

import com.emu.backend.repository.MessdatenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
	@Autowired
	private MessdatenRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
