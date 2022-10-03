package de.jawb.keysafe.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication(
		scanBasePackageClasses = {
				Application.class
		},
		exclude = {
//				SecurityAutoConfiguration.class,
//				UserDetailsServiceAutoConfiguration.class
		}
)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
	}

}

