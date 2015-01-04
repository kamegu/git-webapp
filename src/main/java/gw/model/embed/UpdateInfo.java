package gw.model.embed;

import gw.core.LoginContext;
import gw.core.jpa.EntityUpdateInfo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter @Setter
public class UpdateInfo implements EntityUpdateInfo {
  @Column(name = "REGISTERED_TS")
  private Timestamp registeredTimestamp;
  @Column(name = "UPDATED_TS")
  private Timestamp updatedTimestamp;
  @Column(name = "REGISTERED_ACCOUNT")
  private String registeredAccout;
  @Column(name = "UPDATED_ACCOUNT")
  private String updatedAccout;

  @Override
  public void onUpdate(LoginContext loginContext) {
    this.updatedTimestamp = new Timestamp(System.currentTimeMillis());
    this.updatedAccout = loginContext.getName();
  }

  @Override
  public void onCreate(LoginContext loginContext) {
    this.registeredTimestamp = new Timestamp(System.currentTimeMillis());
    this.updatedTimestamp = this.registeredTimestamp;
    this.registeredAccout = loginContext.getName();
    this.updatedAccout = loginContext.getName();
  }
}
