package gw.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = SmtpAuthValidator.class)
public @interface SmtpAuth {
  Class<?>[] groups() default {};

  String message() default "username and password are necessary";

  Class<? extends Payload>[] payload() default {};

}
