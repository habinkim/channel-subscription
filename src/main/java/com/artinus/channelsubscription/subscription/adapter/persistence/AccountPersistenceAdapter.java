package com.artinus.channelsubscription.subscription.adapter.persistence;

import com.artinus.channelsubscription.common.stereotype.PersistenceAdapter;
import com.artinus.channelsubscription.subscription.application.port.output.LoadAccountPort;
import com.artinus.channelsubscription.subscription.application.port.output.SaveAccountPort;
import com.artinus.channelsubscription.subscription.domain.RegisteredAccount;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements LoadAccountPort, SaveAccountPort {

    private final AccountJpaRepository accountJpaRepository;

    @Override
    public Optional<RegisteredAccount> findByPhoneNumber(String phoneNumber) {
        return findEntityByPhoneNumber(phoneNumber)
                .map(accountEntity -> new RegisteredAccount(accountEntity.getId(), accountEntity.getPhoneNumber(),
                        accountEntity.getCurrentSubscriptionStatus())
                );
    }

    @Override
    public RegisteredAccount createAccount(String phoneNumber) {
        AccountJpaEntity build = AccountJpaEntity.builder().phoneNumber(phoneNumber).build();
        AccountJpaEntity savedAccount = accountJpaRepository.save(build);

        return new RegisteredAccount(savedAccount.getId(), savedAccount.getPhoneNumber(),
                savedAccount.getCurrentSubscriptionStatus());
    }

    @Override
    public RegisteredAccount updateCurrentSubscriptionStatus(String phoneNumber, SubscriptionStatus status) {
        Optional<AccountJpaEntity> byPhoneNumber = findEntityByPhoneNumber(phoneNumber);

        if (byPhoneNumber.isPresent()) {
            AccountJpaEntity updatedAccount = byPhoneNumber.get()
                    .toBuilder().currentSubscriptionStatus(status).build();
            AccountJpaEntity savedAccount = accountJpaRepository.save(updatedAccount);
            return new RegisteredAccount(savedAccount.getId(), savedAccount.getPhoneNumber(),
                    savedAccount.getCurrentSubscriptionStatus());
        }

        throw new IllegalArgumentException("Account not found");
    }

    public Optional<AccountJpaEntity> findEntityByPhoneNumber(String phoneNumber) {
        return accountJpaRepository.findByPhoneNumber(phoneNumber);
    }
}
