package com.artinus.channelsubscription;

import org.springframework.boot.SpringApplication;

public class TestChannelSubscriptionApplication {

    public static void main(String[] args) {
        SpringApplication.from(ChannelSubscriptionApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
