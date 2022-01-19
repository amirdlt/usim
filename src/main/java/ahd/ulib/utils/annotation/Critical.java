package ahd.ulib.utils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PARAMETER })
public @interface Critical {
    String REPEATEDLY_USE = "RepeatedlyUsed";
    String PERFORMANCE_NECESSITY = "PerformanceNecessity";
    String RESOURCE_HEAVY_USAGE = "HeavyResourceUsage";

    String[] reasons() default {};
}
