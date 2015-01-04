package gw.validator;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ TYPE, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PasswordRequiredValidator.class)
public @interface PasswordRequired {
  Class<?>[] groups() default {};

  String message() default "password necessary";

  Class<? extends Payload>[] payload() default {};

}
