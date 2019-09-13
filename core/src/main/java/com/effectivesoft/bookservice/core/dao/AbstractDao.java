package com.effectivesoft.bookservice.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public abstract class AbstractDao<T> implements BaseDao<T> {

    @PersistenceContext
    EntityManager entityManager;

    protected abstract Class<T> getEntityType();

    public Optional<T> create(T obj) {
        entityManager.persist(obj);
        return Optional.ofNullable(obj);
    }

    public Optional<T> read(String id) {
        return Optional.ofNullable(entityManager.find(getEntityType(), id));
    }

    public Optional<T> update(T obj) {
        entityManager.merge(obj);
        return Optional.ofNullable(obj);
    }

    public void delete(String id) {
        T obj = entityManager.find(getEntityType(), id);
        entityManager.remove(obj);
    }
}
