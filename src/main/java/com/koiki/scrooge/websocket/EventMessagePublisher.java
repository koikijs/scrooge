package com.koiki.scrooge.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koiki.scrooge.event.Event;
import com.koiki.scrooge.event.EventRepository;
import com.koiki.scrooge.event.EventRes;
import com.koiki.scrooge.scrooge.ScroogeRepository;
import com.koiki.scrooge.scrooge.ScroogeReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventMessagePublisher {
	private final SimpleWebSocketHandler simpleWebSocketHandler;
	private final EventRepository eventRepository;
	private final ScroogeRepository scroogeRepository;

	public void publishByEventId(String eventId) {
		EventRes eventRes = eventRepository.findById(eventId)
				.map(event -> {
					EventRes er = new EventRes(event);
					er.setScrooges(scroogeRepository.findByEventId(event.getId()));
					return er;
				})
				.orElse(new EventRes());

		try {
			simpleWebSocketHandler.publishMessages(eventId, eventRes);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
