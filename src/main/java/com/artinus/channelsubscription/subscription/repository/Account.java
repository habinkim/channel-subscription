package com.artinus.channelsubscription.subscription.repository;

import com.artinus.channelsubscription.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

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

    @Column(name = "name", nullable = false)
    @Comment("이름")
    private String name;

}
