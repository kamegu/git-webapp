package gw.core.validation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import jersey.repackaged.com.google.common.collect.Iterables;

import org.hibernate.validator.internal.engine.path.NodeImpl;

final class ConstraintUtils {
  private ConstraintUtils() {
  }

  public static String paramName(final ConstraintViolation<?> violation) {
    NodeImpl leafNode = (NodeImpl) Iterables.getLast(violation.getPropertyPath());
    if (leafNode.getKind() == ElementKind.BEAN) {
      return null;
    }
    try {
      Method method = null;
      Parameter parameter = null;
      for (NodeImpl current = leafNode; current.getParent() != null; current = current.getParent()) {
        if (current.getKind().equals(ElementKind.PARAMETER)) {
          NodeImpl parent = current.getParent();
          method = violation.getRootBeanClass().getMethod(parent.getName(), parent.getParameterTypes().toArray(new Class<?>[0]));
          parameter = method.getParameters()[current.getParameterIndex()];
          break;
        }
      }
      if (method == null || parameter == null) {
        return null;
      }

      Consumes consumes = method.getAnnotation(Consumes.class);
      if (consumes != null) {
        if (Arrays.asList(consumes.value()).contains(MediaType.APPLICATION_JSON)) {
          String path = violation.getPropertyPath().toString();
          String paramName = path.replaceAll("(^.*\\.arg[0-9]+\\.)(.+)", "$2");
          return paramName;
        } else if (Arrays.asList(consumes.value()).contains(MediaType.APPLICATION_FORM_URLENCODED)) {
          BeanParam beanParam = parameter.getAnnotation(BeanParam.class);
          if (beanParam != null) {
            Field field = parameter.getType().getDeclaredField(leafNode.getName());
            if (field != null && field.getAnnotation(FormParam.class) != null) {
              return field.getAnnotation(FormParam.class).value();
            }
          }
          FormParam formParam = parameter.getAnnotation(FormParam.class);
          if (formParam != null) {
            return formParam.value();
          }
        }
      }

    } catch (NoSuchMethodException | SecurityException | NoSuchFieldException ex) {
      Logger.getLogger(AjaxValidationErrorExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
}
