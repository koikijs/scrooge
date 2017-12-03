package com.koiki.scrooge.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koiki.scrooge.event.EventRepository;
import com.koiki.scrooge.event.EventRes;
import com.koiki.scrooge.event.ScroogeService;
import com.koiki.scrooge.scrooge.ScroogeRepository;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleWebSocketHandler extends TextWebSocketHandler {
	private final ObjectMapper objectMapper;
	private final EventRepository eventRepository;
	private final ScroogeRepository scroogeRepository;
	private final ScroogeService scroogeService;

	private ConcurrentHashMap<String, Set<WebSocketSession>> eventSessionPool = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String eventId = session.getUri().getQuery();

		eventSessionPool.compute(eventId, (key, sessions) -> {
			if (sessions == null) {
				sessions = new CopyOnWriteArraySet<>();
			}
			sessions.add(session);

			singleCastBySession(session, eventId);

			return sessions;
		});

		log.info("size of eventSessionPool: {}", eventSessionPool.size());
	}

	/**
	 * This method will be invoked when this API received a WebSocket message from clients.
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// nothing to do
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("session closed");
		String eventId = session.getUri().getQuery();

		eventSessionPool.compute(eventId, (key, sessions) -> {

			sessions.remove(session);
			if (sessions.isEmpty()) {
				sessions = null;
			}

			return sessions;
		});
	}

	public void singleCastBySession(WebSocketSession session, String eventId) {
		EventRes eventRes = scroogeService.makeScroogeReq(eventId)
				.orElse(new EventRes());

		try {
			String message = objectMapper.writeValueAsString(eventRes);
			session.sendMessage(new TextMessage(message));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void multiCastByEventId(String eventId) {
		EventRes eventRes = scroogeService.makeScroogeReq(eventId)
				.orElse(new EventRes());

		try {
			String message = objectMapper.writeValueAsString(eventRes);

			if (!eventSessionPool.isEmpty()) {
				for (WebSocketSession eventSession : eventSessionPool.get(eventId)) {
					eventSession.sendMessage(new TextMessage(message));
					log.info("multi cast!");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
