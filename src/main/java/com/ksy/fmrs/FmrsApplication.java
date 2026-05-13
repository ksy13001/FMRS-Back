package com.ksy.fmrs;

import com.ksy.fmrs.dto.FuzzyMappingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@EnableJpaAuditing
@EnableConfigurationProperties(FuzzyMappingProperties.class)
@SpringBootApplication
public class FmrsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FmrsApplication.class, args);
	}

}
