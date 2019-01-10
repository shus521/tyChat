package com.tyss;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                    .title("田园小站")
                    .description("聊天+小功能")
                    .contact("田园")
                    .version("V0.1")
                    .build();
    }

    @Bean
    public Docket createApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.tyss"))
                    .paths(PathSelectors.any())
                    .build();
    }
}
