package net.drapuria.framework.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.beans.annotation.PostDestroy;
import net.drapuria.framework.util.Stacktrace;
import net.drapuria.framework.util.TypeResolver;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class JSONRepository<T, ID extends Serializable> implements CrudRepository<T, ID> {

    private static final Gson staticGson = new Gson();

    private final InMemoryRepository<T, ID> inMemoryRepository;

    protected boolean useGson = true;
    protected Gson gson;

    private final File jsonFile;
    private final Class<T> daoType;
    @Getter
    private final Class<ID> key;

    public JSONRepository() {
        this(new File(FrameworkMisc.PLATFORM.getDataFolder(), "repositories/json/{JSON_REPOSITORY}.json"));
    }

    public JSONRepository(File jsonFile) {
        final Class<?>[] types = findIdType();
        daoType = (Class<T>) types[0];
        key = (Class<ID>) types[1];
        this.inMemoryRepository = new InMemoryRepository<T, ID>(daoType, key) {

            @Override
            public void init() {

            }

            @Override
            public Class<?> type() {
                return daoType;
            }
        };
        if (jsonFile.getPath().endsWith("{JSON_REPOSITORY}.json"))
            jsonFile = new File(jsonFile.getPath().replace("{JSON_REPOSITORY}", this.getClass().getName()));
        this.jsonFile = jsonFile;
        if (!jsonFile.getParentFile().exists())
            jsonFile.getParentFile().mkdirs();
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        initGson();
        try {
            FileReader reader = new FileReader(jsonFile);
            Class clazzArray = Class.forName("[L" + daoType.getName() + ";");
            T[] array =  gson.fromJson(reader, TypeToken.get(clazzArray).getType());
            final List<T> list = array == null ? null : new ArrayList<>(Arrays.asList(array));
            reader.close();
            if (list != null)
                list.forEach(this::save);
        } catch (FileNotFoundException e) {
            Stacktrace.print(e.getMessage(), e);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @PostDestroy
    public void saveToFile() {
        try {
            final List<T> items = this.inMemoryRepository.stream().collect(Collectors.toList());
            final FileWriter fileWriter = new FileWriter(jsonFile);
            gson.toJson(items, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Stacktrace.print(e.getMessage(), e);
        }
    }

    @Override
    public Class<?> type() {
        return daoType;
    }

    @Override
    public <S extends T> S save(S pojo) {
        return inMemoryRepository.save(pojo);
    }

    @Override
    public Optional<T> findById(ID id) {
        return inMemoryRepository.findById(id);
    }

    @Override
    public void deleteById(ID id) {
        this.inMemoryRepository.deleteById(id);
    }

    @Override
    public Optional<T> findBy(String field, Object key) {
        return this.inMemoryRepository.findBy(field, key);
    }

    @Override
    public <Q> Optional<T> findByQuery(String query, Q value) {
        return this.inMemoryRepository.findByQuery(query, value);
    }

    @Override
    public <Q> void deleteByQuery(String query, Q value) {
        this.inMemoryRepository.deleteByQuery(query, value);
    }

    @Override
    public Iterable<T> findAll() {
        return inMemoryRepository.findAll();
    }

    @Override
    public Iterable<T> findAllById(List<ID> ids) {
        return inMemoryRepository.findAllById(ids);
    }

    @Override
    public Stream<T> stream() {
        return this.inMemoryRepository.stream();
    }

    @Override
    public boolean existsById(ID id) {
        return this.inMemoryRepository.existsById(id);
    }

    @Override
    public long count() {
        return this.inMemoryRepository.count();
    }

    @Override
    public void deleteAll() {
        this.inMemoryRepository.deleteAll();
    }


    @SuppressWarnings("unchecked")
    private Class<?>[] findIdType() {
        TypeResolver.enableCache();
        return TypeResolver.resolveRawArguments(JSONRepository.class, this.getClass());
    }

    protected void initGson() {
        this.gson = staticGson;
    }

    @Override
    public void saveAll() {
        this.saveToFile();
    }

    public InMemoryRepository<T, ID> getInMemoryRepository() {
        return inMemoryRepository;
    }

    @Override
    public void init() {

    }
}