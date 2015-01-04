package gw.validator;

import gw.dto.admin.MailJson;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class SmtpAuthValidator implements ConstraintValidator<SmtpAuth, MailJson> {

  @Override
  public void initialize(SmtpAuth arg0) {
  }

  @Override
  public boolean isValid(MailJson form, ConstraintValidatorContext arg1) {
    if (form.isAuth()) {
      return StringUtils.isNotBlank(form.getUsername()) && StringUtils.isNotBlank(form.getPassword());
    }
    return true;
  }
}
