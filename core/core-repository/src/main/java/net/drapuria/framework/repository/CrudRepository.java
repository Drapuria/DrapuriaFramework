/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface CrudRepository<T, ID extends Serializable> extends Repository<T, ID>{

    default void saveAll() {
        for (T t : findAll()) {
            save(t);
        }
    }

    void init();

    Class<?> type();

    <S extends T> S save(S pojo);

    Optional<T> findById(ID id);

    void deleteById(ID id);

    Optional<T> findBy(String field, Object key);

    <Q> Optional<T> findByQuery(String query, Q value);

    <Q> void deleteByQuery(String query, Q value);

    Iterable<T> findAll();

    Iterable<T> findAllById(List<ID> ids);

    Stream<T> stream();

    boolean existsById(ID id);

    long count();

    void deleteAll();

}
