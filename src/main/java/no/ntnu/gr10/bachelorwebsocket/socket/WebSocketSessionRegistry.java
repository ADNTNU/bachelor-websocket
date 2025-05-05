package no.ntnu.gr10.bachelorwebsocket.socket;


import no.ntnu.gr10.bachelorwebsocket.rabbit.RabbitEntity;
import no.ntnu.gr10.bachelorwebsocket.rabbit.RabbitListenerManager;
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

  private String generateKey(String companyId, RabbitEntity entity) {
    return companyId + ":" + entity.toString();
  }

  public void register(String companyId, RabbitEntity entity, WebSocketSession session) {
    logger.info("Registering " + generateKey(companyId, entity));
    String key = generateKey(companyId, entity);
    sessions.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(session);

    // If this is the first subscriber, create a listener
    if (sessions.get(key).size() == 1) {
      logger.info("Creating RabbitMQ listener for " + key);
      rabbitListenerManager.createRabbitListener(companyId, entity);
    }
  }

  public void unregister(String companyId, RabbitEntity entity, WebSocketSession session) {
    logger.info("Unregistering " + generateKey(companyId, entity));
    Set<WebSocketSession> set = sessions.get(generateKey(companyId, entity));
    if (set != null) {
      logger.info("Removing session from " + generateKey(companyId, entity));
      set.remove(session);
      if (set.isEmpty()) sessions.remove(generateKey(companyId, entity));
    }
  }

  public Set<WebSocketSession> getSessions(String companyId, RabbitEntity entity) {
    return sessions.getOrDefault(generateKey(companyId, entity), Set.of());
  }
}
