package gw.validator;

import gw.dto.admin.UserJson;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class PasswordRequiredValidator implements ConstraintValidator<PasswordRequired, UserJson> {

  @Override
  public void initialize(PasswordRequired arg0) {
  }

  @Override
  public boolean isValid(UserJson form, ConstraintValidatorContext arg1) {
    return form != null && StringUtils.isNotBlank(form.getPassword());
  }
}
