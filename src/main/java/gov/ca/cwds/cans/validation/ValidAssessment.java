package gov.ca.cwds.cans.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/** @author denys.davydov */
@Target({TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidAssessmentValidator.class)
@Documented
public @interface ValidAssessment {

  String message() default "Assessment is invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
