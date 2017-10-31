package com.koiki.scrooge.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * This is a handler class to handle WebSocket manually without STOMP and SockJS.
 *
 * http://blog.enjoyxstudy.com/entry/2017/05/10/000000
 * http://www.devglan.com/spring-boot/spring-websocket-integration-example-without-stomp
 */
@Component
public class SimpleWebSocketHandler extends TextWebSocketHandler {

	private ConcurrentHashMap<String, Set<WebSocketSession>> eventSessionPool = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		String eventId = session.getUri().getQuery();

		eventSessionPool.compute(eventId, (key, sessions) -> {

			if (sessions == null) {
				sessions = new CopyOnWriteArraySet<>();
			}
			sessions.add(session);

			return sessions;
		});
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		String eventId = session.getUri().getQuery();

		for (WebSocketSession eventSession : eventSessionPool.get(eventId)) {
			eventSession.sendMessage(message);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		String eventId = session.getUri().getQuery();

		eventSessionPool.compute(eventId, (key, sessions) -> {

			sessions.remove(session);
			if (sessions.isEmpty()) {
				sessions = null;
			}

			return sessions;
		});
	}
}
