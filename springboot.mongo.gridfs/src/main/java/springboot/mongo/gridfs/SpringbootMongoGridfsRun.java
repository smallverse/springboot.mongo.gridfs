package springboot.mongo.gridfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author zhaijunfeng
 */
@ApiIgnore
@RestController
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableSwagger2
public class SpringbootMongoGridfsRun {

	@RequestMapping("/")
	public String hello() {
		return "springboot.mongo.gridfs is good";
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMongoGridfsRun.class, args);
	}

	/**
	 * 允许跨域
	 * 
	 * @return
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}


}
