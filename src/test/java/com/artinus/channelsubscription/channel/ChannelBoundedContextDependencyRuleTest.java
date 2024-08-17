package com.artinus.channelsubscription.channel;

import com.artinus.channelsubscription.base.BoundedContextDependencyRuleTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

public class ChannelBoundedContextDependencyRuleTest extends BoundedContextDependencyRuleTest {

    private static final String BOUNDED_CONTEXT_PACKAGE = "channel";

    @Override
    public String getBoundedContextPackage() {
        return BOUNDED_CONTEXT_PACKAGE;
    }

    @Execution(CONCURRENT)
    @Test
    @DisplayName("Channel Bounded Context satisfied Hexagonal Architecture.")
    void checkAccountDependencyRule() {
        checkDependencyRule();
    }
}
