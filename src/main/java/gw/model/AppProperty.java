package gw.model;

import gw.model.embed.UpdateInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "APP_PROPERTY")
@NamedQuery(name = "AppProperty.findByPrefix", query = "SELECT ap FROM AppProperty ap WHERE ap.name LIKE CONCAT(:prefix, '%')")
@Getter @Setter
@ToString
@NoArgsConstructor
public class AppProperty implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String SYSTEM_PROPERTY_PREFIX = "system.";
  public static final String SYSTEM_URL = SYSTEM_PROPERTY_PREFIX + "url";
  public static final String SYSTEM_NAME = SYSTEM_PROPERTY_PREFIX + "name";

  public static final String SMTP_PROPERTY_PREFIX = "smtp.";
  public static final String SMTP_HOST = SMTP_PROPERTY_PREFIX + "host"; // smtp.gmail.com
  public static final String SMTP_PORT = SMTP_PROPERTY_PREFIX + "port"; // 587
  public static final String SMTP_AUTH = SMTP_PROPERTY_PREFIX + "auth";
  public static final String SMTP_TLS = SMTP_PROPERTY_PREFIX + "starttls.enable";
  public static final String SMTP_FROM = SMTP_PROPERTY_PREFIX + "from";
  public static final String SMTP_USERNAME = SMTP_PROPERTY_PREFIX + "username";
  public static final String SMTP_PASSWORD = SMTP_PROPERTY_PREFIX + "password";
  public static final String SMTP_CONFIRMED = SMTP_PROPERTY_PREFIX + "confirmed";

  @Id
  @Column(name = "NAME")
  @Size(max = 200)
  private String name;

  @Column(name = "VAL")
  @Size(max = 200)
  private String value;

  @Embedded
  private UpdateInfo updateInfo;

  public AppProperty(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public static Optional<String> getValue(List<AppProperty> properties, String name) {
    return properties.stream().filter(prop -> prop.getName().equals(name))
        .findFirst()
        .map(prop -> prop.getValue());
  }
}
