package net.drapuria.framework.bukkit.reflection.resolver.wrapper.impl;

import com.comphenix.protocol.wrappers.collection.ConvertedMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.FieldWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.GuavaWrappers;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.SignedPropertyWrapper;


import java.util.UUID;

public abstract class GameProfileImplementation {

    public static GameProfileImplementation getImplementation() {
        try {
            Class.forName("com.mojang.authlib.GameProfile");

            return new v1_8();
        } catch (ClassNotFoundException ignored) {
            return new v1_7();
        }
    }

    public abstract Class<?> getGameProfileClass();

    public abstract Object create(String name, UUID uuid);

    public abstract UUID getUuid(Object handle);

    public abstract String getName(Object handle);

    public abstract void setUuid(Object handle, UUID uuid);

    public abstract void setName(Object handle, String name);

    public abstract Multimap<String, SignedPropertyWrapper> getProperties(Object handle);

    public static class v1_8 extends GameProfileImplementation {

        private final FieldWrapper<UUID> uuidField;
        private final FieldWrapper<String> nameField;

        public v1_8() {
            try {
                FieldResolver fieldResolver = new FieldResolver(com.mojang.authlib.GameProfile.class);
                this.uuidField = fieldResolver.resolveByFirstTypeDynamic(UUID.class);
                this.nameField = fieldResolver.resolveByFirstTypeDynamic(String.class);
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public Class<?> getGameProfileClass() {
            return com.mojang.authlib.GameProfile.class;
        }

        @Override
        public Object create(String name, UUID uuid) {
            return new com.mojang.authlib.GameProfile(uuid, name);
        }

        @Override
        public UUID getUuid(Object handle) {
            return ((com.mojang.authlib.GameProfile) handle).getId();
        }

        @Override
        public String getName(Object handle) {
            return ((com.mojang.authlib.GameProfile) handle).getName();
        }

        @Override
        public void setUuid(Object handle, UUID uuid) {
            this.uuidField.set(handle, uuid);
        }

        @Override
        public void setName(Object handle, String name) {
            this.nameField.set(handle, name);
        }

        @Override
        public Multimap<String, SignedPropertyWrapper> getProperties(Object handle) {
            com.mojang.authlib.GameProfile gameProfile = (com.mojang.authlib.GameProfile) handle;

            return new ConvertedMultimap<String, Object, SignedPropertyWrapper>(GuavaWrappers.warpMultimap(gameProfile.getProperties())) {

                @Override
                protected SignedPropertyWrapper toOuter(Object property) {
                    return new SignedPropertyWrapper(property);
                }

                @Override
                protected Object toInnerObject(Object outer) {
                    if (outer instanceof SignedPropertyWrapper) {
                        return toInner((SignedPropertyWrapper) outer);
                    }
                    return outer;
                }

                @Override
                protected Object toInner(SignedPropertyWrapper signedPropertyWrapper) {
                    return signedPropertyWrapper.getHandle();
                }

            };
        }
    }

    public static class v1_7 extends GameProfileImplementation {

        private final FieldWrapper<UUID> uuidField;
        private final FieldWrapper<String> nameField;

        public v1_7() {
            try {
                FieldResolver fieldResolver = new FieldResolver(GameProfile.class);
                this.uuidField = fieldResolver.resolveByFirstTypeDynamic(UUID.class);
                this.nameField = fieldResolver.resolveByFirstTypeDynamic(String.class);
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public Class<?> getGameProfileClass() {
            return GameProfile.class;
        }

        @Override
        public Object create(String name, UUID uuid) {
            return new GameProfile(uuid, name);
        }

        @Override
        public UUID getUuid(Object handle) {
            return ((GameProfile) handle).getId();
        }

        @Override
        public String getName(Object handle) {
            return ((GameProfile) handle).getName();
        }

        @Override
        public void setUuid(Object handle, UUID uuid) {
            this.uuidField.set(handle, uuid);
        }

        @Override
        public void setName(Object handle, String name) {
            this.nameField.set(handle, name);
        }

        @Override
        public Multimap<String, SignedPropertyWrapper> getProperties(Object handle) {
            GameProfile gameProfile = (GameProfile) handle;

            return new ConvertedMultimap<String, Object, SignedPropertyWrapper>(GuavaWrappers.warpMultimap(gameProfile.getProperties())) {

                @Override
                protected SignedPropertyWrapper toOuter(Object property) {
                    return new SignedPropertyWrapper(property);
                }

                @Override
                protected Object toInnerObject(Object outer) {
                    if (outer instanceof SignedPropertyWrapper) {
                        return toInner((SignedPropertyWrapper) outer);
                    }
                    return outer;
                }

                @Override
                protected Object toInner(SignedPropertyWrapper signedPropertyWrapper) {
                    return signedPropertyWrapper.getHandle();
                }

            };
        }
    }

}
