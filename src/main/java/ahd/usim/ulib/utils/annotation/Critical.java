package ahd.usim.ulib.utils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PARAMETER })
public @interface Critical {
    String REPEATEDLY_USE = "Repeatedly_Used";
    String PERFORMANCE_NECESSITY = "Performance_Necessity";

    String[] reasons() default {};
}
