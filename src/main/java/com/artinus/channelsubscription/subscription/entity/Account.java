package com.artinus.channelsubscription.subscription.entity;

import com.artinus.channelsubscription.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

/**
 * 서비스를 구독하거나 구독해제 행위를 하는 주체다.
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "account")
public class Account extends BaseEntity {

    @Column(name = "phone_number", unique = true, nullable = false)
    @Comment("전화번호")
    private String phoneNumber;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "current_subscription_status", nullable = false)
    @Comment("현재 구독 상태")
    private SubscriptionStatus currentSubscriptionStatus = SubscriptionStatus.NONE;

}
