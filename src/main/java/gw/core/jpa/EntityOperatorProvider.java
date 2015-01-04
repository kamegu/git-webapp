package gw.core.jpa;

import javax.inject.Inject;

import com.google.inject.Provider;

public class EntityOperatorProvider {
  @Inject private static Provider<EntityOperator> provider;

  public static EntityOperator get() {
      return provider.get();
  }
}
