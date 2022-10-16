/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.redis;

import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.*;
import net.drapuria.framework.beans.annotation.*;
import net.drapuria.framework.configuration.yaml.SimpleYamlConfiguration;
import net.drapuria.framework.configuration.yaml.configs.yaml.YamlConfiguration;
import net.drapuria.framework.libraries.annotation.MavenDependency;
import net.drapuria.framework.libraries.annotation.MavenRepository;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.io.File;
import java.nio.file.Path;

@Service(name = "redis")
@ServiceDependency(dependencies = {"serializer", "jackson"})
public class RedisService {

    private RedissonClient client;

    private boolean enabled;

    @ShouldInitialize
    public boolean shouldInitialize() {
        return enabled;
    }

    public RedisService() {
        File configFile = new File(DrapuriaCommon.PLATFORM.getDataFolder(), "redis.yml");
        if (!configFile.exists()) {
            DrapuriaCommon.PLATFORM.saveResources("redis.yml", false);
        }
        enabled = new RedisConfiguration().isEnabled();
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

    class RedisConfiguration extends YamlConfiguration {

        private boolean enabled;

        protected RedisConfiguration() {
            super(new File(DrapuriaCommon.PLATFORM.getDataFolder(), "redis.yml").toPath());
            loadAndSave();
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

}
