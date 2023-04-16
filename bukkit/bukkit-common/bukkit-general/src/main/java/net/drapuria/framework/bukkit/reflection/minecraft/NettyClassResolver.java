package net.drapuria.framework.bukkit.reflection.minecraft;


import net.drapuria.framework.bukkit.reflection.resolver.ClassResolver;

public class NettyClassResolver extends ClassResolver {

    @Override
    public Class resolve(String... names) throws ClassNotFoundException {
        for (int i = 0; i < names.length; i++) {
            if (!names[i].startsWith(Minecraft.NETTY_PREFIX)) {
                names[i] = Minecraft.NETTY_PREFIX + names[i];
            }
        }
        return super.resolve(names);
    }



    @Override
    public Class resolveSubClass(Class<?> mainClass, String... names) throws ClassNotFoundException {
        String prefix = mainClass.getName() + "$";

        if (!prefix.startsWith(Minecraft.NETTY_PREFIX)) {
            prefix = Minecraft.NETTY_PREFIX + prefix;
        }

        for (int i = 0; i < names.length; i++) {
            names[i] = prefix + names[i];
        }
        return super.resolve(names);
    }
}
