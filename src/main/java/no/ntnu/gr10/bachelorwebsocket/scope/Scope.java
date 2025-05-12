package no.ntnu.gr10.bachelorwebsocket.scope;

public enum Scope {
  FISHERY_ACTIVITY("fishery-activity"),
  FISHING_FACILITY("fishing-facility");

  private final String key;

  Scope(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return key;
  }

  public static Scope fromString(String key) {
    for (Scope scope : Scope.values()) {
      if (scope.toString().equalsIgnoreCase(key)) {
        return scope;
      }
    }
    throw new IllegalArgumentException("No constant with key " + key + " found");
  }
}
