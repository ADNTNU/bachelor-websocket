package no.ntnu.gr10.bachelorwebsocket.rabbit;

import lombok.RequiredArgsConstructor;
import no.ntnu.gr10.bachelorwebsocket.scope.Scope;
import no.ntnu.gr10.bachelorwebsocket.socket.CompanyEntityWebSocketHandler;
import no.ntnu.gr10.bachelorwebsocket.socket.WebSocketSessionRegistry;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RabbitListenerManager {

  private final ConnectionFactory connectionFactory;
  // Map to hold active listeners for companyId-entity pairs
  private final Map<String, MessageListenerContainer> activeListeners = new ConcurrentHashMap<>();

  // Creates a RabbitMQ listener for the specified companyId-entity pair
  public void createRabbitListener(String companyId, Scope scope, RabbitQueueType queueType, CompanyEntityWebSocketHandler handler) {
    String dynamicQueueName = RabbitQueueUtils.getDynamicQueueName(companyId, scope.toString(), queueType);

    // Create the RabbitMQ queue if it doesn't exist
    RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
    Queue queue = new Queue(dynamicQueueName, true); // durable
    rabbitAdmin.declareQueue(queue);

    // Create the listener container
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(dynamicQueueName);

    // Set the message listener
    container.setMessageListener(message -> {
      String messageJson = new String(message.getBody());
      processMessage(messageJson, companyId, handler);
    });

    // Start the listener container
    container.start();
    activeListeners.put(dynamicQueueName, container);

    System.out.println("Listener created for queue: " + dynamicQueueName);
  }

  // Stops the RabbitMQ listener for the specified companyId-entity pair
  public void stopRabbitListener(String companyId, Scope scope, RabbitQueueType queueType) {
    String dynamicQueueName = RabbitQueueUtils.getDynamicQueueName(companyId, scope.toString(), queueType);
    MessageListenerContainer container = activeListeners.remove(dynamicQueueName);
    if (container != null) {
      container.stop();
      System.out.println("Listener stopped for queue: " + dynamicQueueName);
    }
  }

  // Process the RabbitMQ message
  private void processMessage(String messageJson, String companyId, CompanyEntityWebSocketHandler handler) {
    try {
      handler.broadcastToCompany(companyId, messageJson);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}