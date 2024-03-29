package net.drapuria.framework.bukkit.reflection.resolver;

import net.drapuria.framework.bukkit.reflection.resolver.wrapper.MethodWrapper;
import net.drapuria.framework.util.AccessUtil;

import java.lang.reflect.Method;

/**
 * Resolver for methods
 */
public class MethodResolver extends MemberResolver<Method> {


	public MethodResolver(Class<?> clazz) {
		super(clazz);
	}

	public MethodResolver(String className) throws ClassNotFoundException {
		super(className);
	}

	public Method resolveSignature(String... signatures)throws ReflectiveOperationException {
		for (Method method : clazz.getDeclaredMethods()) {
			String methodSignature = MethodWrapper.getMethodSignature(method);
			for (String s : signatures) {
				if (s.equals(methodSignature)) {
					return AccessUtil.setAccessible(method);
				}
			}
		}
		return null;
	}

	public MethodWrapper resolve(int index, Class<?>... parameters) throws ReflectiveOperationException {

		return new MethodWrapper(this.resolve(new ResolverQuery(index, parameters)));

	}

	public Method resolveSignatureSilent(String... signatures) {
		try {
			return resolveSignature(signatures);
		} catch (ReflectiveOperationException ignored) {
		}
		return null;
	}

	public MethodWrapper resolveSignatureWrapper(String... signatures) {
		return new MethodWrapper(resolveSignatureSilent(signatures));
	}

	@Override
	public Method resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException {
		return AccessUtil.setAccessible(this.clazz.getDeclaredMethods()[index]);
	}

	@Override
	public Method resolveIndexSilent(int index) {
		try {
			return resolveIndex(index);
		} catch (IndexOutOfBoundsException | ReflectiveOperationException ignored) {
		}
		return null;
	}

	@Override
	public MethodWrapper resolveIndexWrapper(int index) {
		return new MethodWrapper<>(resolveIndexSilent(index));
	}

	public MethodWrapper resolveWrapper(String... names) {
		return new MethodWrapper<>(resolveSilent(names));
	}

	public MethodWrapper resolveWrapper(ResolverQuery... queries) {
		return new MethodWrapper<>(resolveSilent(queries));
	}

	public Method resolveSilent(String... names) {
		try {
			return resolve(names);
		} catch (Exception e) {
			if (AccessUtil.VERBOSE) { e.printStackTrace(); }
		}
		return null;
	}

	@Override
	public Method resolveSilent(ResolverQuery... queries) {
		return super.resolveSilent(queries);
	}

	public Method resolve(String... names) throws NoSuchMethodException {
		ResolverQuery.Builder builder = ResolverQuery.builder();
		for (String name : names) {
			builder.with(name);
		}
		return resolve(builder.build());
	}

	@Override
	public Method resolve(ResolverQuery... queries) throws NoSuchMethodException {
		try {
			return super.resolve(queries);
		} catch (ReflectiveOperationException e) {
			throw (NoSuchMethodException) e;
		}
	}

	@Override
	protected Method resolveObject(ResolverQuery query) throws ReflectiveOperationException {
		return this.accessorCache.resolveMethod(query);

	}

	@Override
	protected NoSuchMethodException notFoundException(String joinedNames) {
		return new NoSuchMethodException("Could not resolve method for " + joinedNames + " in class " + this.clazz);
	}

	public MethodWrapper resolve(Class<?> returnType, int index, Class<?>... parameters) throws ReflectiveOperationException {

		return new MethodWrapper<>(this.resolve(new ResolverQuery(returnType, index, parameters)));

	}

	static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;
		if (l1.length != l2.length) { return false; }
		for (int i = 0; i < l1.length; i++) {
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		}
		return equal;
	}
}
