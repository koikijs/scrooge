package com.koiki.scrooge.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test/websocket")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {
	private final SimpleWebSocketHandler simpleWebSocketHandler;

	@GetMapping("/{eventId}")
	public ResponseEntity<?> get(@PathVariable String eventId) throws Exception {

		simpleWebSocketHandler.publishMessages(eventId, null);

		return ResponseEntity.ok().body(null);
	}
}
