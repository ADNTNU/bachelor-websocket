package no.ntnu.gr10.bachelorwebsocket.socket;

import no.ntnu.gr10.bachelorwebsocket.rabbit.RabbitQueueType;
import no.ntnu.gr10.bachelorwebsocket.scope.Scope;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;

public class CompanyEntityWebSocketHandler extends TextWebSocketHandler {

  private final WebSocketSessionRegistry registry;
  private final Scope scope;

  public CompanyEntityWebSocketHandler(WebSocketSessionRegistry registry, Scope scope) {
    this.registry = registry;
    this.scope = scope;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    String companyId = extractCompanyId(session);
    if (companyId == null) {
      session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing companyId"));
      return;
    }

    registry.register(companyId, scope, RabbitQueueType.CREATE, session, this);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    String companyId = extractCompanyId(session);
    if (companyId != null) {
      registry.unregister(companyId, scope, RabbitQueueType.CREATE, session);
    }
  }

  public void broadcastToCompany(String companyId, String message) {
    for (WebSocketSession session : registry.getSessions(companyId, scope)) {
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
