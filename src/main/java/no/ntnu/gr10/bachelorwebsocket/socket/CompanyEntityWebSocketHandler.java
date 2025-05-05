package no.ntnu.gr10.bachelorwebsocket.socket;

import no.ntnu.gr10.bachelorwebsocket.rabbit.RabbitEntity;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;

public class CompanyEntityWebSocketHandler extends TextWebSocketHandler {

  private final WebSocketSessionRegistry registry;
  private final RabbitEntity entityType;

  public CompanyEntityWebSocketHandler(WebSocketSessionRegistry registry, RabbitEntity entity) {
    this.registry = registry;
    this.entityType = entity;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    String companyId = extractCompanyId(session);
    if (companyId == null) {
      session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing companyId"));
      return;
    }

    registry.register(companyId, entityType, session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    String companyId = extractCompanyId(session);
    if (companyId != null) {
      registry.unregister(companyId, entityType, session);
    }
  }

  public void broadcastToCompany(String companyId, String message) {
    for (WebSocketSession session : registry.getSessions(companyId, entityType)) {
      if (session.isOpen()) {
        try {
          session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private String extractCompanyId(WebSocketSession session) throws NullPointerException {
    return UriComponentsBuilder.fromUri(Objects.requireNonNull(session.getUri())).build().getQueryParams().getFirst("companyId");
  }
}
