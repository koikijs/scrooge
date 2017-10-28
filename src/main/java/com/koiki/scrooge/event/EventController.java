package com.koiki.scrooge.event;

import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
	private final EventRepository eventRepository;

	@PostMapping
	public ResponseEntity<?> post(@RequestBody Event event) {
		Event savedEvent = eventRepository.save(event);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedEvent.getId()).toUri();

		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{eventId}")
	public ResponseEntity<?> get(@PathVariable String eventId) {
		return eventRepository.findById(eventId)
				.map(event -> {
					return ResponseEntity.ok().body(event);
				})
				.orElse(ResponseEntity.notFound().build());
	}
}

