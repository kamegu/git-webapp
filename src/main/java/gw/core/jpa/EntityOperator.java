package gw.core.jpa;

import gw.core.LoginContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class EntityOperator {
  @Inject private HttpServletRequest servletRequest;

  public void onCreate(EntityUpdateInfo updateInfo) {
    LoginContext loginContext = LoginContext.get(servletRequest);
    updateInfo.onCreate(loginContext);
  };

  public void onUpdate(EntityUpdateInfo updateInfo) {
    LoginContext loginContext = LoginContext.get(servletRequest);
    updateInfo.onUpdate(loginContext);
  };

  Optional<EntityUpdateInfo> getEntityUpdateInfo(Object object) {
    if (object.getClass().getAnnotation(Entity.class) == null) {
      throw new EntityUpdateInfoException("エンティティを指定してください:" + object.getClass().getName());
    }
    Field[] fields = object.getClass().getDeclaredFields();
    Optional<Field> match = Arrays.stream(fields)
        .filter(field -> EntityUpdateInfo.class.isAssignableFrom(field.getType()))
        .findFirst();

    return match.map(field -> {
      field.setAccessible(true);
      try {
          if (field.get(object) == null) {
            field.set(object, field.getType().newInstance());
          }
          return (EntityUpdateInfo) field.get(object);
      } catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
          e.printStackTrace();
          throw new EntityUpdateInfoException("更新者情報の登録に失敗しました:" + object.getClass().getName());
      }
    });
  }
}
