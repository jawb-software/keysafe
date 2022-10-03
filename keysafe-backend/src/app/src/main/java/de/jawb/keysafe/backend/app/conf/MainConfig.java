package de.jawb.keysafe.backend.app.conf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import de.jawb.keysafe.backend.app.endpoint.interceptor.DurationLogInterceptor;
import de.jawb.keysafe.backend.app.endpoint.resolvers.ApiCallerInfoArgumentResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@EnableAsync
@Configuration
public class MainConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DurationLogInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ApiCallerInfoArgumentResolver());
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("keysafe backend API")
                        .description("keysafe backend application")
                        .version("v1.0.0"));
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(new LocalDateTimeSerializer(formatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));

        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(module);
    }

}
