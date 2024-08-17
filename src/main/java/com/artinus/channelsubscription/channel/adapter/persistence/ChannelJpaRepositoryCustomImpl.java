package com.artinus.channelsubscription.channel.adapter.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChannelJpaRepositoryCustomImpl implements ChannelJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QChannelJpaEntity CHANNEL = QChannelJpaEntity.channelJpaEntity;

    @Override
    public Boolean existsByName(final String name) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(CHANNEL)
                .where(CHANNEL.name.eq(name), CHANNEL.available.isTrue())
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Boolean existsByChannelId(final Long channelId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(CHANNEL)
                .where(CHANNEL.id.eq(channelId), CHANNEL.available.isTrue())
                .fetchFirst();

        return fetchOne != null;
    }
}
