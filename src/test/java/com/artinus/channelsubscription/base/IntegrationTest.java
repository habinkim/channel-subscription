package com.artinus.channelsubscription.base;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Target(TYPE)
@Retention(RUNTIME)
@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        properties = {"spring.main.allow-bean-definition-overriding=true"},
        webEnvironment = RANDOM_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public @interface IntegrationTest {
}
