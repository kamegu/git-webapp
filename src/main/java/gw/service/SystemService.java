package gw.service;

import gw.core.action.Result;
import gw.dto.admin.SystemJson;
import gw.model.AppProperty;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class SystemService {
  @Inject private Provider<EntityManager> emProvider;

  public SystemJson getSystemSetting() {
    List<AppProperty> appProperties = getAppProperties();
    return new SystemJson(appProperties);
  }

  @Transactional
  public Result<Boolean> registerSettings(SystemJson systemSetting) {
    getAppProperties().forEach(prop -> {
      emProvider.get().remove(prop);
    });

    List<AppProperty> appProperties = systemSetting.createAppProperties();
    appProperties.stream()
    .filter(prop -> Objects.nonNull(prop.getValue()))
    .forEach(prop -> {
      emProvider.get().persist(prop);
    });

    return Result.success(true);
  }

  private List<AppProperty> getAppProperties() {
    return emProvider.get()
        .createNamedQuery("AppProperty.findByPrefix", AppProperty.class)
        .setParameter("prefix", AppProperty.SYSTEM_PROPERTY_PREFIX)
        .getResultList();
  }
}
