package no.ntnu.gr10.bachelorwebsocket.rabbit;

public class RabbitQueueUtils {
  public static String getDynamicQueueName(String companyId, String entity) {
    return companyId + "-" + entity + "-queue"; // Generate dynamic queue name
  }
}
