package gw.dto.admin;

import gw.model.Account;
import gw.model.UserAccount;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UserFullJson {
  private AccountJson account = new AccountJson();
  private boolean admin;
  private boolean group = false;
  private Timestamp registeredTimestamp;

  public UserFullJson(Account accountEntity) {
    this.account = new AccountJson(accountEntity);
    UserAccount userAccount = accountEntity.getUserAccount();
    if (userAccount != null) {
      this.admin = userAccount.isAdmin();
    }
    this.group = accountEntity.isGroup();
    this.registeredTimestamp = accountEntity.getUpdateInfo().getRegisteredTimestamp();
  }
}
