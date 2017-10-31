package com.koiki.scrooge.event;

import com.koiki.scrooge.scrooge.Scrooge;
import com.koiki.scrooge.scrooge.ScroogeRepository;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*") //TODO change origin to suitable one
public class EventController {
	private final EventRepository eventRepository;
	private final ScroogeRepository scroogeRepository;

	@PostMapping
	public ResponseEntity<?> postEvent(@RequestBody Event event) {
		Event savedEvent = eventRepository.save(event);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedEvent.getId())
				.toUri();

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

	@PostMapping("/{eventId}/scrooges")
	public ResponseEntity<?> postScrooge(
			@RequestBody Scrooge scrooge,
			@PathVariable String eventId) {
		scrooge.setEventId(eventId);
		Scrooge savedScrooge = scroogeRepository.save(scrooge);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{scroogeId}")
				.buildAndExpand(eventId, savedScrooge.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();
	}
}

