package com.refresh.customer_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adventureWorksOpenApi() {
        return new OpenAPI().info(new Info()
                .title("AdventureWorks API")
                .description("REST API untuk data AdventureWorks (Spring Boot + SQL Server)")
                .version("v1"));
    }
}