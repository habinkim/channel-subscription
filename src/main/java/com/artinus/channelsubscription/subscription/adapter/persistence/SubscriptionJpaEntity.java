package com.artinus.channelsubscription.subscription.adapter.persistence;

import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaEntity;
import com.artinus.channelsubscription.common.entity.BaseEntity;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
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
public class SubscriptionJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @Comment("계정 ID")
    private AccountJpaEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @Comment("채널 ID")
    private ChannelJpaEntity channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_subscription_status", nullable = false)
    @Comment("이전 구독 상태")
    private SubscriptionStatus previousSubscriptionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    @Comment("구독 상태")
    private SubscriptionStatus subscriptionStatus;

}
