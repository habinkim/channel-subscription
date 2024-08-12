package com.artinus.channelsubscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChannelSubscriptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelSubscriptionApplication.class, args);
    }

}
