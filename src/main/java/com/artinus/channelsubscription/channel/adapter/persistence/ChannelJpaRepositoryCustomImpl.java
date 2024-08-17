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
    public Boolean existsByName(String name) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(CHANNEL)
                .where(CHANNEL.name.eq(name))
                .fetchFirst();

        return fetchOne != null;
    }
}
