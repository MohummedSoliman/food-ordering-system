package com.mohamed.outbox;

public interface OutboxScheduler {

    void processOutboxMessage();
}
