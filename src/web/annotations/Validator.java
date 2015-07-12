package web.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Validators.class)
public @interface Validator {
    String param();
    Class type() default String.class;
    boolean required() default false;
}
