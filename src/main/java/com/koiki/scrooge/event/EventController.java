package com.koiki.scrooge.event;

import com.koiki.scrooge.scrooge.Scrooge;
import com.koiki.scrooge.scrooge.ScroogeRepository;
import com.koiki.scrooge.scrooge.ScroogeReq;
import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@CrossOrigin(
		origins = "*", //TODO change origin to suitable one
		allowedHeaders = {"Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma", "Location"},
		exposedHeaders = {"Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma", "Location"}
)
public class EventController {
	private final EventRepository eventRepository;
	private final ScroogeRepository scroogeRepository;

	@PostMapping
	public ResponseEntity<?> postEvent(@Valid @RequestBody EventReq eventReq) {
		Event savedEvent = eventRepository.save(new Event(eventReq));

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
					EventRes eventRes = new EventRes(event);
					eventRes.setScrooges(scroogeRepository.findByEventId(event.getId()));
					return ResponseEntity.ok().body(eventRes);
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/{eventId}/scrooges")
	public ResponseEntity<?> postScrooge(
			@RequestBody ScroogeReq scroogeReq,
			@PathVariable String eventId) {

		if (!eventRepository.findById(eventId).isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Scrooge scrooge = new Scrooge(scroogeReq);
		scrooge.setEventId(eventId);
		Scrooge savedScrooge = scroogeRepository.save(scrooge);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{scroogeId}")
				.buildAndExpand(savedScrooge.getId())
				.toUri();

		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{eventId}/scrooges/{scroogeId}")
	public ResponseEntity<?> getScrooge(
			@PathVariable String eventId,
			@PathVariable String scroogeId) {

		Optional<Scrooge> scrooge = scroogeRepository.findById(scroogeId);
		if (!scrooge.isPresent()) {
			return ResponseEntity.notFound().build();
		} else if (!scrooge.get().getEventId().equals(eventId)) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().body(scrooge.get());
		}
	}

	@DeleteMapping("/{eventId}/scrooges/{scroogeId}")
	public ResponseEntity<?> deleteScrooge(
			@PathVariable String eventId,
			@PathVariable String scroogeId) {

		Optional<Scrooge> scrooge = scroogeRepository.findById(scroogeId);
		if (!scrooge.isPresent()) {
			return ResponseEntity.notFound().build();
		} else if (!scrooge.get().getEventId().equals(eventId)) {
			return ResponseEntity.notFound().build();
		} else {
			scroogeRepository.deleteById(scroogeId);
			return ResponseEntity.noContent().build();
		}
	}
}
