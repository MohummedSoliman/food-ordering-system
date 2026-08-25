package com.mohamed.saga;

public interface SagaStep<T> {

    void proces(T data);

    void rollback(T data);
}
