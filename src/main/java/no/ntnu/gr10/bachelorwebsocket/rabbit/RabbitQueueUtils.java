package no.ntnu.gr10.bachelorwebsocket.rabbit;

public class RabbitQueueUtils {
  public static String getDynamicQueueName(String companyId, String entity, RabbitQueueType type) {
    return "producer." + entity + "." + type.toString() + "." + "company-" + companyId;
  }
}
