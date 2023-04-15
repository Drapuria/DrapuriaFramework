package net.drapuria.framework.bukkit.reflection.resolver;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.NonNull;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ClassWrapper;
import net.drapuria.framework.util.AccessUtil;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * Default {@link ClassResolver}
 */
public class ClassResolver extends ResolverAbstract<Class> {

	private static final LoadingCache<String, Class<?>> CLASS_CACHE = Caffeine
			.newBuilder()
			.expireAfterAccess(1L, TimeUnit.MINUTES)
			.build(new CacheLoader<String, Class<?>>() {
				@Override
				public @Nullable Class<?> load(@NonNull String s) {
					try {
						return Class.forName(s);
					} catch (ClassNotFoundException ex) {
						return null;
					}
				}
			});

	public ClassWrapper resolveWrapper(String... names) {
		return new ClassWrapper<>(resolveSilent(names));
	}

	public Class resolveSilent(String... names) {
		try {
			return resolve(names);
		} catch (Exception e) {
			if (AccessUtil.VERBOSE) { e.printStackTrace(); }
		}
		return null;
	}

	public Class resolve(String... names) throws ClassNotFoundException {
		ResolverQuery.Builder builder = ResolverQuery.builder();
		for (String name : names)
			builder.with(name);
		try {
			return super.resolve(builder.build());
		} catch (ReflectiveOperationException e) {
			throw (ClassNotFoundException) e;
		}
	}

	@Override
	protected Class resolveObject(ResolverQuery query) throws ReflectiveOperationException {
		Class<?> result = CLASS_CACHE.get(query.getName());

		if (result == null) {
			throw new ClassNotFoundException();
		}

		return result;
	}

	@Override
	protected ClassNotFoundException notFoundException(String joinedNames) {
		return new ClassNotFoundException("Could not resolve class for " + joinedNames);
	}

	public void cache(String name, Class<?> type) {
		CLASS_CACHE.put(name, type);
	}
}
