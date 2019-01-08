package com.tyss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//扫描mybasic mapper的包路径
@MapperScan(basePackages = "com.tyss.mapper")
@ComponentScan(basePackages= {"com.tyss", "org.n3r.idworker"})
public class Application {

	@Bean
	public SpringUtil getSpingUtil() {
		return new SpringUtil();
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
