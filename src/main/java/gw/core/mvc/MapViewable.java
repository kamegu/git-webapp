package gw.core.mvc;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.server.mvc.Viewable;

public class MapViewable extends Viewable {

  private Map<String, Object> map;

  @SuppressWarnings("unchecked")
  public MapViewable(String templateName) throws IllegalArgumentException {
    super(templateName, new HashMap<>());
    map = (Map<String, Object>) this.getModel();
  }

  public MapViewable add(String key, Object value) {
    this.map.put(key, value);
    return this;
  }

  public MapViewable add(String key1, Object value1, String key2, Object value2) {
    this.map.put(key1, value1);
    this.map.put(key2, value2);
    return this;
  }
}
