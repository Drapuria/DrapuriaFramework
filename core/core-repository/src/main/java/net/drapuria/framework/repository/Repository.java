package net.drapuria.framework.repository;

import net.drapuria.framework.services.DisallowAnnotation;
import net.drapuria.framework.services.PreInitialize;
import net.drapuria.framework.services.ServiceDependency;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@DisallowAnnotation(value = PreInitialize.class)
@ServiceDependency(dependencies = {"mongodb", "sql"})
public interface Repository<T, ID extends Serializable> {

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

    boolean existsById(ID id);

    long count();

    void deleteAll();

}
