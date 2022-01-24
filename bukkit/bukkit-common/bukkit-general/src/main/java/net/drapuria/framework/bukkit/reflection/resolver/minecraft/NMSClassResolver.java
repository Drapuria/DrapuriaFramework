package net.drapuria.framework.bukkit.reflection.resolver.minecraft;

import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import net.drapuria.framework.bukkit.reflection.resolver.ClassResolver;

/**
 * {@link ClassResolver} for <code>net.minecraft.server.*</code> classes
 */
public class NMSClassResolver extends ClassResolver {

	@Override
	public Class resolve(String... names) throws ClassNotFoundException {
		for (int i = 0; i < names.length; i++) {
			if (names[i].startsWith("net.minecraft"))
				continue;

			if (names[i].contains(".") && MinecraftVersion.VERSION.hasNMSVersionPrefix()) {
				/* use class name only */
				String[] path = names[i].split("\\.");
				names[i] = Minecraft.getNMSPackage() + "." + path[path.length - 1];
				continue;
			}

			/* use the whole name */
			names[i] = Minecraft.getNMSPackage() + "." + names[i];
		}
		return super.resolve(names);
	}


	public Class resolveSubClass(Class<?> mainClass, String... names) throws ClassNotFoundException {
		String prefix = mainClass.getName() + "$";

		if (!prefix.startsWith(Minecraft.getNMSPackage())) {
			prefix = Minecraft.getNMSPackage() + "." + prefix;

		}

		for (int i = 0; i < names.length; i++) {
			names[i] = prefix + names[i];
		}
		return super.resolve(names);
	}

}
