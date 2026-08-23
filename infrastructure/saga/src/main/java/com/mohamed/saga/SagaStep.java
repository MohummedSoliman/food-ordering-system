package com.mohamed.saga;

import com.mohamed.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent, U extends DomainEvent> {

    S proces(T data);

    U rollback(T data);
}
