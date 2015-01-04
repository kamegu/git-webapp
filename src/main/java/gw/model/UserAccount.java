package gw.model;

import gw.model.embed.UpdateInfo;
import gw.types.PasswordType;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "USER_ACCOUNT")
@Getter
@Setter
@ToString(of = { "name" })
public class UserAccount implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "NAME")
  @NotNull
  @Size(max = 100)
  private String name;

  @Column(name = "FULL_NAME")
  @Size(max = 100)
  private String fullName;

  @Column(name = "MAIL_ADDRESS")
  @Size(max = 100)
  private String mailAddress;

  @Column(name = "ADMINISTRATOR")
  @NotNull
  private boolean admin;

  @Column(name = "PASSWORD")
  @NotNull
  @Size(max = 100)
  private String password;

  @Column(name = "PASSWORD_TYPE")
  @Enumerated(EnumType.STRING)
  private PasswordType passwordType;

  @Column(name = "SALT")
  @NotNull
  @Size(min = 32, max = 32)
  private String salt;

  @Column(name = "LAST_LOGIN_DATE")
  private Timestamp lastLoginDate;

  @Embedded
  private UpdateInfo updateInfo;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  private Account account;

  public boolean authenticate(String input) {
    return passwordType.encrypt(input, this.getSalt()).equals(this.getPassword());
  }
}
