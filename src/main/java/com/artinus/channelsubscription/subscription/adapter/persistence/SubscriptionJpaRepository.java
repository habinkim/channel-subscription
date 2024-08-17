package com.artinus.channelsubscription.subscription.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, Long>, SubscriptionJpaRepositoryCustom {
}
