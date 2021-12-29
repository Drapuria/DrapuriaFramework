package net.drapuria.framework.libraries.annotation;

import java.lang.annotation.*;

/**
 * Represents a maven dependency
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenDependency {

    /**
     * @return The GroupId of the Artifact
     */
    String groupId();

    /**
     * @return The Artifact Id
     */
    String artifactId();

    /**
     * @return The Version of the Artifact
     */
    String version();

    /**
     * @return The Version of the package
     */
    String versionPackage() default "";

    /**
     * @return The Repository where the maven artifact is stored.
     */
    MavenRepository repo() default @MavenRepository(url = "https://repo1.maven.org/maven2/");

}
