package net.drapuria.framework.bukkit.reflection.resolver;


import net.drapuria.framework.bukkit.reflection.accessor.ClassAccessorCache;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.WrapperAbstract;

import java.lang.reflect.Member;

/**
 * abstract class to resolve members
 *
 * @param <T> member type
 * @see ConstructorResolver
 * @see FieldResolver
 * @see MethodResolver
 */
public abstract class MemberResolver<T extends Member> extends ResolverAbstract<T> {

	protected Class<?> clazz;
	protected ClassAccessorCache accessorCache;

	public MemberResolver(Class<?> clazz, ClassAccessorCache accessorCache) {
		if (clazz == null) { throw new IllegalArgumentException("class cannot be null"); }
		this.clazz = clazz;
		this.accessorCache = accessorCache;
	}

	public MemberResolver(Class<?> clazz) {
		this(clazz, ClassAccessorCache.get(clazz));
	}

	public MemberResolver(String className) throws ClassNotFoundException {
		this(new ClassResolver().resolve(className));
	}

	/**
	 * Resolve a member by its index
	 *
	 * @param index index
	 * @return the member
	 * @throws IndexOutOfBoundsException    if the specified index is out of the available member bounds
	 * @throws ReflectiveOperationException if the object could not be set accessible
	 */
	public abstract T resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException;

	/**
	 * Resolve member by its index (without exceptions)
	 *
	 * @param index index
	 * @return the member or <code>null</code>
	 */
	public abstract T resolveIndexSilent(int index);

	/**
	 * Resolce member wrapper by its index
	 *
	 * @param index index
	 * @return the wrapped member
	 */
	public abstract WrapperAbstract resolveIndexWrapper(int index);

}
