package ahd.ulib.utils.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD })
public @interface Constraint {
    String[] reasons() default {};
}
