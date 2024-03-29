/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.repository;

import lombok.SneakyThrows;
import net.drapuria.framework.util.TypeResolver;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @param <T> Object to cache
 * @param <ID> ID of the object - has to be the first object in the list
 */
public abstract class InMemoryRepository<T, ID extends Serializable> implements CrudRepository<T, ID> {

    private final Map<String, Field> fieldCache = new HashMap<>();
    private boolean isExtendedFieldCache = false;
    private final Map<String, Map<Object, T>> extendedFieldCache = new HashMap<>();

    protected final Map<ID, T> storage = new HashMap<>();
    private Field keyField;

    private final Class<T> daoType;
    private final Class<ID> key;

    @SuppressWarnings("unchecked")
    public InMemoryRepository() {
        Class<?>[] types = findIdType();
        daoType = (Class<T>) types[0];
        key = (Class<ID>) types[1];
        keyField = Arrays.stream(daoType.getDeclaredFields()).filter(field -> field.getType() == key)
                .findFirst()
                .orElse(null);
        if (keyField != null)
            keyField.setAccessible(true);
        init();
    }

    public InMemoryRepository(Class<T> daoType, Class<ID> key) {
        this.daoType = daoType;
        this.key = key;
        this.keyField = Arrays.stream(daoType.getDeclaredFields()).filter(field -> field.getType() == key)
                .findFirst()
                .orElse(null);
        if (keyField != null)
            keyField.setAccessible(true);
        this.init();
    }

    @Override
    public Iterable<T> findAll() {
        return storage.values();
    }


    @Override
    public Iterable<T> findAllById(List<ID> ids) {
        return storage.entrySet()
                .stream()
                .filter(entry -> ids.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(ID id) {
        this.storage.remove(id);
    }

    @Override
    public <Q> void deleteByQuery(String query, Q value) {
        throw new UnsupportedOperationException("Cannot delete by query in in memory repository.");
    }

    public void enableExtendedFieldCache() {
        this.isExtendedFieldCache = true;
    }

    public void disableExtendedFieldCache() {
        this.isExtendedFieldCache = false;
        this.extendedFieldCache.clear();
    }

    public void refreshExtendedFieldCache() {
        this.extendedFieldCache.clear();
    }

    @Override
    public void deleteAll() {
        this.storage.clear();
        this.extendedFieldCache.clear();
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public Stream<T> stream() {
        return this.storage.values().stream();
    }

    @Override
    public boolean existsById(ID id) {
        return this.storage.containsKey(id);
    }

    @Override
    public <Q> Optional<T> findByQuery(String query, Q value) {
        return Optional.empty();
    }

    @SneakyThrows
    @Override
    public Optional<T> findBy(String field, Object key) {
        if (this.isExtendedFieldCache && this.extendedFieldCache.containsKey(field)) {
            final Map<Object, T> fieldCache = new HashMap<>();
            if (fieldCache.containsKey(key))
                return Optional.of(fieldCache.get(key));
        }
        Field declaredField;
        if (!fieldCache.containsKey(field)) {
            declaredField = daoType.getDeclaredField(field);
            declaredField.setAccessible(true);
            fieldCache.put(field, declaredField);
        } else {
            declaredField = fieldCache.get(field);
        }
        for (T deo : storage.values()) {
            if (declaredField.get(deo).equals(key)) {
                if (this.isExtendedFieldCache) {
                    if (!this.extendedFieldCache.containsKey(field))
                        this.extendedFieldCache.put(field, new HashMap<>());
                    this.extendedFieldCache.get(field).put(key, deo);
                }
                return Optional.of(deo);
            }
        }
        return Optional.empty();
    }

    @SneakyThrows
    public Optional<T> findByEqualsIgnoreCase(String field, String key) {
        Field declaredField;
        if (!fieldCache.containsKey(field)) {
            declaredField = daoType.getDeclaredField(field);
            declaredField.setAccessible(true);
            fieldCache.put(field, declaredField);
        } else {
            declaredField = fieldCache.get(field);
        }
        if (declaredField.getType() == String.class) {
            for (T deo : storage.values()) {
                if (((String)declaredField.get(deo)).equalsIgnoreCase(key)) {
                    return Optional.of(deo);
                }
            }
        } else {
            for (T deo : storage.values()) {
                if (declaredField.get(deo).equals(key)) {
                    return Optional.of(deo);
                }
            }
        }
        return Optional.empty();
    }

    @SneakyThrows
    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> S save(S pojo) {
        ID id = null;
        if (keyField == null) {
            for (Field field : pojo.getClass().getDeclaredFields()) {
                if (field.getType() == key) {
                    this.keyField = field;
                    field.setAccessible(true);
                    id = (ID) field.get(pojo);
                    break;
                }
            }
        } else {
            id = (ID) keyField.get(pojo);
        }
        if (id == null)
            return null;
        return (S) storage.put(id, pojo);
    }

    @SuppressWarnings("unchecked")
    public <S extends T> S save(S pojo, ID id) {
        return (S) storage.put(id, pojo);
    }

    @SuppressWarnings("unchecked")
    private Class<?>[] findIdType() {
        TypeResolver.enableCache();
        return  TypeResolver.resolveRawArguments(InMemoryRepository.class, getClass());
    }
}
