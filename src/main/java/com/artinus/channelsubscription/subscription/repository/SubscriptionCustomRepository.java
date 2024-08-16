package com.artinus.channelsubscription.subscription.repository;

import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SubscriptionCustomRepository {

    Map<String, List<SubscriptionHistory>> findAllByPhoneNumber(@NotBlank String phoneNumber);

    List<SubscriptionHistory> findAllByChannelIdAndDate(Long channelId, LocalDate date);
}
