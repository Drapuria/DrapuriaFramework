package net.drapuria.framework.redis;

import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.*;
import net.drapuria.framework.beans.annotation.*;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.io.File;

@Service(name = "redis")
@ServiceDependency(dependencies = {"serializer", "jackson"})
public class RedisService {

    private RedissonClient client;

    @ShouldInitialize
    public boolean shouldInitialize() {
        return true;
    }

    public RedisService() {
        File configFile = new File(DrapuriaCommon.PLATFORM.getDataFolder(), "redis.yml");
        if (!configFile.exists()) {
            DrapuriaCommon.PLATFORM.saveResources("redis.yml", false);
        }
    }

    @SneakyThrows
    @PreInitialize
    public void initClient() {

        this.client = Redisson.create(Config.fromYAML(new File(DrapuriaCommon.PLATFORM.getDataFolder(), "redis.yml"))
                .setCodec(new JsonJacksonCodec(JacksonService.INSTANCE.getMainMapper())));
    }

    @PostDestroy
    public void stop() {
        if (this.client != null)
            this.client.shutdown();
    }

    public RReadWriteLock getLock(String name) {
        return this.client.getReadWriteLock(name);
    }

    public RMap<String, Object> getMap(String name) {
        return this.client.getMap(name);
    }

    public Iterable<String> getKeys(String pattern) {
        return this.client.getKeys().getKeysByPattern(pattern);
    }

    public RedissonClient getClient() {
        return client;
    }
}
