package com.artinus.channelsubscription.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.PrettyPrintingJsonBodyFilter;

import java.time.Clock;

@Lazy
@Slf4j
@Configuration
public class CommonBeans {

    @Bean
    @Qualifier("message")
    public MessageSource messageSource() {
        String basename = "messages/messages";
        String charSet = "UTF-8";
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setDefaultEncoding(charSet);
        return messageSource;
    }

    @Bean
    @Qualifier("validation")
    public MessageSource validationMessageSource() {
        String basename = "messages/validation";
        String charSet = "UTF-8";
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setDefaultEncoding(charSet);

        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean getValidator(@Qualifier("validation") MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .bodyFilter(new PrettyPrintingJsonBodyFilter())
                .build();
    }

}
