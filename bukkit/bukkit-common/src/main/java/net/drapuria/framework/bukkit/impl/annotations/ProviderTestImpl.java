package net.drapuria.framework.bukkit.impl.annotations;


import net.drapuria.framework.bukkit.impl.test.ImplementationTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProviderTestImpl {

    Class<? extends ImplementationTest> value();

}
