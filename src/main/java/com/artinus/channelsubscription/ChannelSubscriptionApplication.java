package com.artinus.channelsubscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EntityScan(basePackages = {"com.artinus.channelsubscription"})
public class ChannelSubscriptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelSubscriptionApplication.class, args);
    }

}
