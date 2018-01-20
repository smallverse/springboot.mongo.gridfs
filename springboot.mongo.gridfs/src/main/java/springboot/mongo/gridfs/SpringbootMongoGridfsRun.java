package springboot.mongo.gridfs;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author zhaijunfeng
 */
@RestController
@SpringBootApplication
public class SpringbootMongoGridfsRun {

    @RequestMapping("/")
    public String hello() {
        return "springboot.mongo.gridfs";
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

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置上传文件大小限制
        factory.setMaxFileSize("1024MB");
        // 设置上传总数据大小
        factory.setMaxRequestSize("1024MB");
        return factory.createMultipartConfig();
    }
}
