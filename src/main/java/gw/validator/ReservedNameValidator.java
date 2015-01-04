package gw.validator;

import gw.core.SystemSetting;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.ArrayUtils;

public class ReservedNameValidator implements ConstraintValidator<ReservedName, String> {

  @Override
  public void initialize(ReservedName reservedName) {
  }

  @Override
  public boolean isValid(String name, ConstraintValidatorContext context) {
    return !ArrayUtils.contains(SystemSetting.RESERVED_USER_NAMES, name);
  }
}
