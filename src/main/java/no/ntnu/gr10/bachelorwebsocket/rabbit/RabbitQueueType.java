package no.ntnu.gr10.bachelorwebsocket.rabbit;

public enum RabbitQueueType {
  CREATE("create"),
  UPDATE("update"),
  DELETE("delete");

  private final String queueName;

  RabbitQueueType(String queueName) {
    this.queueName = queueName;
  }

  @Override
  public String toString() {
    return queueName;
  }

  public static RabbitQueueType fromString(String queueName) {
    for (RabbitQueueType type : RabbitQueueType.values()) {
      if (type.toString().equalsIgnoreCase(queueName)) {
        return type;
      }
    }
    throw new IllegalArgumentException("No enum constant " + RabbitQueueType.class.getCanonicalName() + "." + queueName);
  }
}
