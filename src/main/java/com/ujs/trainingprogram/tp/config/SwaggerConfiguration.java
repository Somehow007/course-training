package com.ujs.trainingprogram.tp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 设置文档 API Swagger 配置信息
 */
@Slf4j
@Configuration
public class SwaggerConfiguration implements ApplicationRunner {

    @Value("${server.port:8090}")
    private String serverPort;
    @Value("${spring.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("培养计划管理系统")
                        .description("统一管理学校内各学院的培养计划管理方案，便于各学院老师更便捷进行下载、更新培养计划")
                        .version("v1.0.0")
                        .contact(new Contact().name("somehow").email("sunhao04012006@163.com"))
                );
    }

    /**
     * 控制台中打印
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("API document: http://127.0.0.1:{}{}/doc.html", serverPort, contextPath);
    }
}
