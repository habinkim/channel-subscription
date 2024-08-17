package com.artinus.channelsubscription.subscription;

import com.artinus.channelsubscription.base.BoundedContextDependencyRuleTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SubscriptionBoundedContextDependencyRuleTest extends BoundedContextDependencyRuleTest {

    private static final String BOUNDED_CONTEXT_PACKAGE = "subscription";

    @Override
    public String getBoundedContextPackage() {
        return BOUNDED_CONTEXT_PACKAGE;
    }

    @Test
    @DisplayName("Subscription Bounded Context satisfied Hexagonal Architecture.")
    void checkSubscriptionDependencyRule() {
        checkDependencyRule();
    }
}
