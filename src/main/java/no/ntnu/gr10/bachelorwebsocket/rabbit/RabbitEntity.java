package no.ntnu.gr10.bachelorwebsocket.rabbit;

public enum RabbitEntity {
  FISHERY_ACTIVITY("fishery-activity");

  private final String queueName;

  RabbitEntity(String queueName) {
    this.queueName = queueName;
  }

  public String getQueueName() {
    return queueName;
  }

  @Override
  public String toString() {
    return queueName;
  }
}
