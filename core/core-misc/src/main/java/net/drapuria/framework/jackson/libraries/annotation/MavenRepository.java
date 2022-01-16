package net.drapuria.framework.jackson.libraries.annotation;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * Represents a maven repository
 */
@Documented
@Target(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenRepository {

    /**
     * @return the base url of the repository
     */
    @Nonnull
    String url();

}
