package com.effectivesoft.bookservice.core.dao;

import java.util.Optional;

public interface BaseDao<T> {
    Optional<T> create(T obj);

    Optional<T> read(String id);

    Optional<T> update(T obj);

    void delete(String id);
}