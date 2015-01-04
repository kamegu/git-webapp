package gw.core.util;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.ws.rs.container.ResourceInfo;

public class ResourceUtils {

  public static <T extends Annotation> Optional<T> getAnnotation(ResourceInfo resourceInfo, Class<T> clazz) {
    Optional<T> result = Optional.ofNullable(resourceInfo.getResourceMethod().getAnnotation(clazz));
    if (result.isPresent()) {
      return result;
    }
    return Optional.ofNullable(resourceInfo.getResourceClass().getAnnotation(clazz));
  }
}
