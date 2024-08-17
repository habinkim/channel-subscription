package com.artinus.channelsubscription.subscription.adapter.persistence;

import com.artinus.channelsubscription.channel.adapter.persistence.QChannelJpaEntity;
import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.fields;
import static com.querydsl.core.types.dsl.Expressions.dateTemplate;

@Repository
@RequiredArgsConstructor
public class SubscriptionJpaRepositoryCustomImpl implements SubscriptionJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QSubscriptionJpaEntity SUBSCRIPTION = QSubscriptionJpaEntity.subscriptionJpaEntity;
    private static final QChannelJpaEntity CHANNEL = QChannelJpaEntity.channelJpaEntity;
    private static final QAccountJpaEntity ACCOUNT = QAccountJpaEntity.accountJpaEntity;

    @Override
    public Map<String, List<SubscriptionHistory>> findAllByPhoneNumber(String phoneNumber) {
        // For PostgreSQL
        DateTemplate<String> extractedDate = dateTemplate(String.class, "TO_CHAR({0}, 'YYYY-MM-DD')", SUBSCRIPTION.createdAt);

        return queryFactory.from(SUBSCRIPTION)
                .join(SUBSCRIPTION.channel, CHANNEL)
                .join(SUBSCRIPTION.account, ACCOUNT)
                .where(ACCOUNT.phoneNumber.eq(phoneNumber))
                .orderBy(SUBSCRIPTION.id.desc())
                .transform(groupBy(extractedDate)
                        .as(list(
                                fields(SubscriptionHistory.class,
                                        SUBSCRIPTION.id.as("subscriptionId"),
                                        ACCOUNT.phoneNumber,
                                        CHANNEL.id.as("channelId"),
                                        CHANNEL.name.as("channelName"),
                                        SUBSCRIPTION.previousSubscriptionStatus.as("previousStatus"),
                                        SUBSCRIPTION.subscriptionStatus.as("status"),
                                        SUBSCRIPTION.createdAt.as("createdAt")
                                )
                        ))
                );

    }

    @Override
    public List<SubscriptionHistory> findAllByChannelIdAndDate(Long channelId, LocalDate date) {
        LocalDateTime startDate = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(date, LocalTime.MAX);

        return queryFactory.select(
                        fields(SubscriptionHistory.class,
                                SUBSCRIPTION.id.as("subscriptionId"),
                                ACCOUNT.phoneNumber,
                                CHANNEL.id.as("channelId"),
                                CHANNEL.name.as("channelName"),
                                SUBSCRIPTION.previousSubscriptionStatus.as("previousStatus"),
                                SUBSCRIPTION.subscriptionStatus.as("status"),
                                SUBSCRIPTION.createdAt.as("createdAt")
                        )
                )
                .from(SUBSCRIPTION)
                .join(SUBSCRIPTION.channel, CHANNEL)
                .join(SUBSCRIPTION.account, ACCOUNT)
                .where(CHANNEL.id.eq(channelId), SUBSCRIPTION.createdAt.between(startDate, endDate))
                .orderBy(SUBSCRIPTION.id.desc())
                .fetch();
    }
}
