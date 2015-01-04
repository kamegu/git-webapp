package gw.service;

import gw.model.UserAccount;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.Provider;

public class LoginService {

  @Inject private Provider<EntityManager> emProvider;

  public UserAccount login(String userName, String password) {
    UserAccount userAccount = emProvider.get().find(UserAccount.class, userName);

    if (userAccount == null) {
      return null;
    }
    String encrypted = userAccount.getPasswordType().encrypt(password, userAccount.getSalt());
    if (userAccount.getPassword().equals(encrypted)) {
      return userAccount;
    }
    return null;
  }
}
