package gw.dto.admin;

import gw.model.AppProperty;
import gw.validator.SmtpAuth;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.validator.constraints.NotEmpty;

@NoArgsConstructor
@Getter @Setter
@SmtpAuth
public class MailJson {
  @NotEmpty
  private String host;
  @NotNull
  private Integer port;
  @NotEmpty
  private String from;
  private boolean auth;
  private String username;
  private String password;
  private boolean tls;
  private boolean confirmed;

  public MailJson(List<AppProperty> appProperties) {
    this.host = AppProperty.getValue(appProperties, AppProperty.SMTP_HOST).orElse(null);
    this.port = AppProperty.getValue(appProperties, AppProperty.SMTP_PORT).map(Integer::parseInt).orElse(null);
    this.from = AppProperty.getValue(appProperties, AppProperty.SMTP_FROM).orElse(null);
    this.auth = BooleanUtils.toBoolean(AppProperty.getValue(appProperties, AppProperty.SMTP_AUTH).orElse(null));
    this.username = AppProperty.getValue(appProperties, AppProperty.SMTP_USERNAME).orElse(null);
    this.password = AppProperty.getValue(appProperties, AppProperty.SMTP_PASSWORD).orElse(null);
    this.tls = BooleanUtils.toBoolean(AppProperty.getValue(appProperties, AppProperty.SMTP_TLS).orElse(null));
    this.confirmed = BooleanUtils.toBoolean(AppProperty.getValue(appProperties, AppProperty.SMTP_CONFIRMED).orElse(null));
  }

  public List<AppProperty> createAppProperties() {
    List<AppProperty> properties = new ArrayList<>();
    properties.add(new AppProperty(AppProperty.SMTP_HOST, getHost()));
    if (getPort() != null) {
      properties.add(new AppProperty(AppProperty.SMTP_PORT, String.valueOf(getPort())));
    }
    properties.add(new AppProperty(AppProperty.SMTP_FROM, getFrom()));
    properties.add(new AppProperty(AppProperty.SMTP_AUTH, BooleanUtils.toStringTrueFalse(isAuth())));
    properties.add(new AppProperty(AppProperty.SMTP_USERNAME, getUsername()));
    properties.add(new AppProperty(AppProperty.SMTP_PASSWORD, getPassword()));
    properties.add(new AppProperty(AppProperty.SMTP_TLS, BooleanUtils.toStringTrueFalse(isTls())));
    properties.add(new AppProperty(AppProperty.SMTP_CONFIRMED, BooleanUtils.toStringTrueFalse(isConfirmed())));

    return properties;
  }
}
