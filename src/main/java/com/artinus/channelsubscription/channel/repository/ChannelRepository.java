package com.artinus.channelsubscription.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Optional<Channel> findByNameAndAvailableTrue(String name);
}
