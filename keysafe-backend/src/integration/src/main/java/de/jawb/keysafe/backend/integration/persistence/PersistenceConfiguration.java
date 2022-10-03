package de.jawb.keysafe.backend.integration.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:persistence.properties")
public class PersistenceConfiguration {

}
