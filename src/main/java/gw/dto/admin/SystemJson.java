package gw.dto.admin;

import gw.model.AppProperty;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class SystemJson {
  private String url;
  private String name;

  public SystemJson(List<AppProperty> appProperties) {
    this.url = AppProperty.getValue(appProperties, AppProperty.SYSTEM_URL).orElse(null);
    this.name = AppProperty.getValue(appProperties, AppProperty.SYSTEM_NAME).orElse(null);
  }

  public List<AppProperty> createAppProperties() {
    List<AppProperty> properties = new ArrayList<>();
    properties.add(new AppProperty(AppProperty.SYSTEM_URL, getUrl()));
    properties.add(new AppProperty(AppProperty.SYSTEM_NAME, getName()));

    return properties;
  }
}
