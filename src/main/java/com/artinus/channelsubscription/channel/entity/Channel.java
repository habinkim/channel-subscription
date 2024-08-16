package com.artinus.channelsubscription.channel.entity;

import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

/**
 * 서비스를 구독하거나 구독해제하기위한 진입점이다.
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "channel")
public class Channel extends BaseEntity {

    @Column(name = "name", unique = true, nullable = false)
    @Comment("채널명")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Comment("채널 타입")
    private ChannelType type;

    @Builder.Default
    @Column(name = "available", nullable = false)
    @Comment("사용 가능 여부")
    private boolean available = true;

}
