package com.mohamed.event;

public interface DomainEvent<T> {
    void fire();
}
