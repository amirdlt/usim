package ahd.ulib.utils.annotation;

import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.Documented;

@Documented
public @interface NotFinal {
    String CONCURRENCY = "Concurrency";
    String PERFORMANCE = "Performance";
    String REIMPLEMENT_NEEDED = "Reimplement_Needed";
    String PLATFORM_DEPENDENCY = "Platform_Dependency";

    @MagicConstant(stringValues = {CONCURRENCY, PERFORMANCE, REIMPLEMENT_NEEDED, PLATFORM_DEPENDENCY}) String[] issues() default {};
}
