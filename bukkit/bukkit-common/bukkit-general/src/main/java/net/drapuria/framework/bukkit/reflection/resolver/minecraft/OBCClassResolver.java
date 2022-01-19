package net.drapuria.framework.bukkit.reflection.resolver.minecraft;

import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.ClassResolver;

/**
 * {@link ClassResolver} for <code>org.bukkit.craftbukkit.*</code> classes
 */
public class OBCClassResolver extends ClassResolver {

    @Override
    public Class resolve(String... names) throws ClassNotFoundException {
        for (int i = 0; i < names.length; i++) {
            if (!names[i].startsWith("org.bukkit")) {
                names[i] = Minecraft.getOBCPackage() + "." + names[i];
            }
        }
        return super.resolve(names);
    }
}
