package net.drapuria.framework.module.annotations;

public @interface ModuleData {

    String name();

    String author();

    String version();

    String[] description() default "";

    String[] moduleDependencies() default "";

}
