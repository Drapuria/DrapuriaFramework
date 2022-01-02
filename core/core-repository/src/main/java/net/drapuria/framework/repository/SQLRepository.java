package net.drapuria.framework.repository;


import net.drapuria.framework.RepositoryType;
import net.drapuria.framework.database.SqlService;
import net.drapuria.framework.database.connection.AbstractConnectionFactory;
import net.drapuria.framework.database.orm.Session;
import net.drapuria.framework.database.orm.Transaction;
import net.drapuria.framework.services.PostInitialize;
import net.drapuria.framework.services.PreDestroy;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class SQLRepository<T, ID extends Serializable> implements Repository<T, ID> {

    private RepositoryType type;
    private UpdatePolicy updatePolicy;
    private AbstractConnectionFactory factory;

    protected InMemoryRepository<T, ID> inMemoryRepository;

    public SQLRepository(UpdatePolicy updatePolicy) {
        this.updatePolicy = updatePolicy;
    }

    public SQLRepository() {
        updatePolicy = UpdatePolicy.INSTANT;
    }

    public SQLRepository(UpdatePolicy updatePolicy, RepositoryType type) {
        this.type = type;
        this.updatePolicy = updatePolicy;
    }

    @PostInitialize
    public void init() {
        if (this.type != null) {
            this.factory = SqlService.getService.factory(this.getClass(), type);
        } else {
            this.factory = SqlService.getService.factory(this.getClass(), null);
        }
        if (updatePolicy == UpdatePolicy.CACHED)
            registerInMemoryRepository();
        this.factory.createTable(this.type());
    }

    public abstract void registerInMemoryRepository();

    @PreDestroy
    public void saveAllCachedData() {
        saveAllCached();
    }

    public <T> T performSessionResult(Function<Session, T> sessionConsumer) {
        if (this.factory == null) {
            throw new IllegalArgumentException("Attempt to perform action before repository initialized!");
        }

        T result = null;

        Transaction transaction = null;
        try {
            transaction = this.factory.startTransaction();

            Session session = this.factory.session(transaction);
            result = sessionConsumer.apply(session);

            transaction.commit();
        } catch (Throwable throwable) {
            if (transaction != null) {
                transaction.rollback();
            }
            throwable.printStackTrace();
            return null;
        }

        return result;
    }

    public void performSession(Consumer<Session> sessionConsumer) {
        Transaction transaction = null;
        try {
            transaction = this.factory.startTransaction();

            Session session = this.factory.session(transaction);
            sessionConsumer.accept(session);

            transaction.commit();
        } catch (Throwable throwable) {
            if (transaction != null) {
                transaction.rollback();
            }
            throwable.printStackTrace();
        }
    }

    @Override
    public <S extends T> S save(S pojo) {
        if (this.updatePolicy == UpdatePolicy.CACHED) {
            return this.inMemoryRepository.save(pojo);
        }
        this.performSession(session -> session.upsert(pojo));
        return pojo;
    }

    public void saveAllCached() {
        if (this.updatePolicy != UpdatePolicy.CACHED)
            return;
        this.inMemoryRepository.findAll().forEach(this::saveCached);
    }

    public void saveCached(ID id) {
        this.performSession(session -> session.update(findById(id)));
    }

    public <S extends T> S saveCached(S pojo) {
        this.performSession(session -> session.upsert(pojo));
        return pojo;
    }

    @Override
    public Optional<T> findById(ID id) {
        if (this.updatePolicy == UpdatePolicy.CACHED) {
            if (!this.inMemoryRepository.existsById(id)) {
                T t = (T) this.performSessionResult(session -> session.find(this.type(), id));
                if (t != null)
                    this.inMemoryRepository.save(t);
            }
            return this.inMemoryRepository.findById(id);
        }
        return (Optional<T>) Optional.ofNullable(this.performSessionResult(session -> session.find(this.type(), id)));
    }

    @Override
    public <Q> Optional<T> findByQuery(String queryName, Q value) {
        return (Optional<T>) Optional.ofNullable(this.performSessionResult(session -> session.findByQuery(this.type(), queryName, value)));
    }

    @Override
    public boolean existsById(ID id) {
        return this.performSessionResult(session -> session.find(this.type(), id) != null);
    }

    @Override
    public Iterable<T> findAll() {
        return (Iterable<T>) this.performSessionResult(session -> session.results(this.type()));
    }

    @Override
    public Iterable<T> findAllById(List<ID> ids) {
        return (Iterable<T>) this.performSessionResult(session -> session.query().byMultipleIds(this.type(), ids).results(this.type()));
    }

    @Override
    public long count() {
        return this.performSessionResult(session -> session.query().count(this.type()).first(Long.class));
    }

    @Override
    public void deleteById(ID id) {
        this.performSession(session -> session.delete(id));
    }

    @Override
    public <Q> void deleteByQuery(String queryName, Q value) {
        this.performSession(session -> session.query()
                .whereQuery(queryName, value)
                .delete());
    }

    @Override
    public void deleteAll() {
        this.performSession(session -> session.query().delete());
    }
}
