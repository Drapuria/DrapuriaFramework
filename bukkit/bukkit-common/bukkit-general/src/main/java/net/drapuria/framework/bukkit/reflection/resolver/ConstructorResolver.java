package net.drapuria.framework.bukkit.reflection.resolver;

import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ConstructorWrapper;
import net.drapuria.framework.util.AccessUtil;
import net.drapuria.framework.util.Utility;

import java.lang.reflect.Constructor;

/**
 * Resolver for constructors
 */
public class ConstructorResolver extends MemberResolver<Constructor> {

    public ConstructorResolver(Class<?> clazz) {
        super(clazz);
    }

    public ConstructorResolver(String className) throws ClassNotFoundException {
        super(className);
    }

    @Override
    public Constructor resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException {
        return AccessUtil.setAccessible(this.clazz.getDeclaredConstructors()[index]);
    }

    @Override
    public Constructor resolveIndexSilent(int index) {
        try {
            return resolveIndex(index);
        } catch (IndexOutOfBoundsException | ReflectiveOperationException ignored) {
        }
        return null;
    }

    @Override
    public ConstructorWrapper resolveIndexWrapper(int index) {
        return new ConstructorWrapper<>(resolveIndexSilent(index));
    }

    public ConstructorWrapper resolveWrapper(Class<?>[]... types) {
        return new ConstructorWrapper<>(resolveSilent(types));
    }

    public Constructor resolveSilent(Class<?>[]... types) {
        try {
            return resolve(types);
        } catch (Exception e) {
			if (AccessUtil.VERBOSE) { e.printStackTrace(); }

        }
        return null;
    }

    public Constructor resolve(Class<?>[]... types) throws NoSuchMethodException {
        ResolverQuery.Builder builder = ResolverQuery.builder();
        for (Class<?>[] type : types)
            builder.with(type);
        try {
            return super.resolve(builder.build());
        } catch (ReflectiveOperationException e) {
            throw (NoSuchMethodException) e;
        }
    }

    @Override
    protected Constructor resolveObject(ResolverQuery query) throws ReflectiveOperationException {
        return AccessUtil.setAccessible(this.clazz.getDeclaredConstructor(query.getTypes()));
    }

    public Constructor resolveFirstConstructor() throws ReflectiveOperationException {
        for (Constructor constructor : this.clazz.getDeclaredConstructors()) {
            return AccessUtil.setAccessible(constructor);
        }
        return null;
    }

    public Constructor resolveFirstConstructorSilent() {
        try {
            return resolveFirstConstructor();
        } catch (Exception e) {
        }
        return null;
    }

    public Constructor resolveLastConstructor() throws ReflectiveOperationException {
        Constructor constructor = null;
        for (Constructor constructor1 : this.clazz.getDeclaredConstructors()) {
            constructor = constructor1;
        }
        if (constructor != null) { return AccessUtil.setAccessible(constructor); }
        return null;
    }

    public Constructor resolveLastConstructorSilent() {
        try {
            return resolveLastConstructor();
        } catch (Exception e) {
        }
        return null;
    }

    public ConstructorWrapper resolveMatches(Class<?>[]... types) {
        for (Class<?>[] parameters : types) {
            for (Constructor constructor : this.clazz.getDeclaredConstructors()) {

                Class<?>[] parametersTypes = constructor.getParameterTypes();

                if (isParametersEquals(parametersTypes, parameters)) {
                    try {
                        return new ConstructorWrapper<>(AccessUtil.setAccessible(constructor));
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected NoSuchMethodException notFoundException(String joinedNames) {
        return new NoSuchMethodException("Could not resolve constructor for " + joinedNames + " in class " + this.clazz);
    }

    static boolean isParametersEquals(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length) { return false; }
        for (int i = 0; i < l1.length; i++) {
            if (Utility.wrapPrimitive(l1[i]) != Utility.wrapPrimitive(l2[i])) {
                equal = false;
                break;
            }
        }
        return equal;
    }
}
