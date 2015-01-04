package gw.core.jpa;

import gw.core.LoginContext;

public interface EntityUpdateInfo {
  void onUpdate(LoginContext loginContext);
  void onCreate(LoginContext loginContext);
}
