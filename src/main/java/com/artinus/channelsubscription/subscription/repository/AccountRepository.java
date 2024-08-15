package com.artinus.channelsubscription.subscription.repository;

import com.artinus.channelsubscription.subscription.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
