package dev.majek.hexnicks.config;

import java.lang.reflect.Type;
import java.util.List;

public enum ConfigType {
  BOOLEAN(Boolean.class),
  STRING(String.class),
  INT(Integer.class),
  COLOR(String.class),
  LIST(List.class);

  private final Type type;

  ConfigType(Type type) {
    this.type = type;
  }

  public Type type() {
    return this.type;
  }
}
