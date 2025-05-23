package no.ntnu.gr10.bachelorwebsocket.socket;


import no.ntnu.gr10.bachelorwebsocket.rabbit.RabbitListenerManager;
import no.ntnu.gr10.bachelorwebsocket.rabbit.RabbitQueueType;
import no.ntnu.gr10.bachelorwebsocket.scope.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class WebSocketSessionRegistry {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
  private final RabbitListenerManager rabbitListenerManager;

  public WebSocketSessionRegistry(RabbitListenerManager rabbitListenerManager) {
    this.rabbitListenerManager = rabbitListenerManager;
  }

  private String generateKey(String companyId, Scope scope) {
    return companyId + ":" + scope.toString();
  }

  public void register(String companyId, Scope scope, RabbitQueueType queueType, WebSocketSession session, CompanyEntityWebSocketHandler handler) {
    logger.info("Registering " + generateKey(companyId, scope));
    String key = generateKey(companyId, scope);
    sessions.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(session);

    // If this is the first subscriber, create a listener
    if (sessions.get(key).size() == 1) {
      logger.info("Creating RabbitMQ listener for " + key);
      rabbitListenerManager.createRabbitListener(companyId, scope,queueType, handler);
    }
  }

  public void unregister(String companyId, Scope scope, RabbitQueueType queueType, WebSocketSession session) {
    logger.info("Unregistering " + generateKey(companyId, scope));
    Set<WebSocketSession> set = sessions.get(generateKey(companyId, scope));
    if (set != null) {
      logger.info("Removing session from " + generateKey(companyId, scope));
      set.remove(session);
      if (set.isEmpty()) {
        sessions.remove(generateKey(companyId, scope));
        logger.info("Stopping RabbitMQ listener for " + generateKey(companyId, scope));
        rabbitListenerManager.stopRabbitListener(companyId, scope, queueType);
      };
    }
  }

  public Set<WebSocketSession> getSessions(String companyId, Scope scope) {
    return sessions.getOrDefault(generateKey(companyId, scope), Set.of());
  }
}
