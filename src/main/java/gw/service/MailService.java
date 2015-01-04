package gw.service;

import gw.core.action.Result;
import gw.dto.admin.MailJson;
import gw.model.AppProperty;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class MailService {
  @Inject private Provider<EntityManager> emProvider;
  @Inject private MailSender mailSender;

  public MailJson getMailSetting() {
    List<AppProperty> appProperties = getAppProperties();
    return new MailJson(appProperties);
  }

  @Transactional
  public Result<Boolean> registerMailSettings(MailJson mailJson) {
    getAppProperties().forEach(prop -> {
      emProvider.get().remove(prop);
    });

    List<AppProperty> appProperties = mailJson.createAppProperties();
    appProperties.stream()
    .filter(prop -> Objects.nonNull(prop.getValue()))
    .forEach(prop -> {
      emProvider.get().persist(prop);
    });

    // reset
    mailSender.initProperties(true);

    return Result.success(true);
  }

  private List<AppProperty> getAppProperties() {
    return emProvider.get()
        .createNamedQuery("AppProperty.findByPrefix", AppProperty.class)
        .setParameter("prefix", AppProperty.SMTP_PROPERTY_PREFIX)
        .getResultList();
  }
}
