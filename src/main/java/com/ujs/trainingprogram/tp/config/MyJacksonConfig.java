package com.ujs.trainingprogram.tp.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;


import java.io.IOException;
import java.util.TimeZone;

@Configuration
public class MyJacksonConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.getSerializerProvider()
                .setNullValueSerializer(new JsonSerializer<Object>() {
                    @Override
                    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                        jsonGenerator.writeString("");
                    }
                });

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, new JsonSerializer<Long>() {
            @Override
            public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(String.valueOf(value));
            }
        });
        simpleModule.addSerializer(Long.TYPE, new JsonSerializer<Long>() {
            @Override
            public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(String.valueOf(value));
            }
        });
        mapper.registerModule(simpleModule);
        
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
