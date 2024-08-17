package com.artinus.channelsubscription.channel.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelJpaRepository extends JpaRepository<ChannelJpaEntity, Long>, ChannelJpaRepositoryCustom {

    Optional<ChannelJpaEntity> findByIdAndAvailableTrue(Long id);

    Optional<ChannelJpaEntity> findByNameAndAvailableTrue(String name);
}
