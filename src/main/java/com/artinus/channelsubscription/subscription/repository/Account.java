package com.artinus.channelsubscription.subscription.repository;

import com.artinus.channelsubscription.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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

}
