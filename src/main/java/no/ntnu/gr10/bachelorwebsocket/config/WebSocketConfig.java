package no.ntnu.gr10.bachelorwebsocket.config;


import no.ntnu.gr10.bachelorwebsocket.scope.Scope;
import no.ntnu.gr10.bachelorwebsocket.socket.CompanyEntityWebSocketHandler;
import no.ntnu.gr10.bachelorwebsocket.socket.WebSocketSessionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final WebSocketSessionRegistry registry;

  public WebSocketConfig(WebSocketSessionRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
            .addHandler(new CompanyEntityWebSocketHandler(this.registry, Scope.FISHERY_ACTIVITY), "/ws/data/fishery-activity")
            .addHandler(new CompanyEntityWebSocketHandler(this.registry, Scope.FISHING_FACILITY), "/ws/data/fishing-facility")
            .setAllowedOrigins("*");
  }
}