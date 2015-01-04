package gw.core.jpa;

import java.util.Optional;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class EntityUpdateInfoListener {
  @PrePersist
  public void prePersist(Object object) {
    EntityOperator operator = EntityOperatorProvider.get();
    getEntityUpdateInfo(object).ifPresent(operator::onCreate);
  }

  @PreUpdate
  public void preUpdate(Object object) {
    EntityOperator operator = EntityOperatorProvider.get();
    getEntityUpdateInfo(object).ifPresent(operator::onUpdate);
  }

  private Optional<EntityUpdateInfo> getEntityUpdateInfo(Object object) {
    return EntityOperatorProvider.get().getEntityUpdateInfo(object);
  }
}
