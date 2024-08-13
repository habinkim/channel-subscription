package com.artinus.channelsubscription.subscription.repository;

import com.artinus.channelsubscription.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

/**
 * 구독하거나, 구독 해제한 이력이다.<p>
 * Append성 History 데이터이다.
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "subscription")
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @Comment("구독한 계정 ID")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @Comment("채널 ID")
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    @Comment("구독 상태")
    private SubscriptionStatus subscriptionStatus;

}
