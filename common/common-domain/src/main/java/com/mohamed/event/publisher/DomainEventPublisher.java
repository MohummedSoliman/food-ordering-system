package com.mohamed.event.publisher;

import com.mohamed.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
