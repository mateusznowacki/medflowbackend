package pl.medflow.medflowbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;



@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class MedflowbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedflowbackendApplication.class, args);
	}

}
