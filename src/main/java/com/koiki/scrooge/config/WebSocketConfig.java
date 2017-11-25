package com.koiki.scrooge.config;

import com.koiki.scrooge.websocket.SimpleWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * This is a config class to implement WebSocket feature without STOMP and SockJS.
 *
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
	private final SimpleWebSocketHandler simpleWebSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(simpleWebSocketHandler, "/")
				.setAllowedOrigins("*"); //TODO set suitable origins
	}
}
