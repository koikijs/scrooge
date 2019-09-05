package com.koiki.scrooge.event;

import com.koiki.scrooge.scrooge.Scrooge;
import com.koiki.scrooge.scrooge.ScroogeRepository;
import com.koiki.scrooge.scrooge.ScroogeReq;
import com.koiki.scrooge.websocket.SimpleWebSocketHandler;
import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(
		origins = {"https://kyoden.now.sh", "http://localhost:3000"},
		allowedHeaders = {"Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma", "Location"},
		exposedHeaders = {"Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma", "Location"},
		allowCredentials = "true"
)
public class EventController {
	private final EventRepository eventRepository;
	private final ScroogeRepository scroogeRepository;
	private final ScroogeService scroogeService;
	private final SimpleWebSocketHandler simpleWebSocketHandler;

	@PostMapping
	public ResponseEntity<?> postEvent(@Valid @RequestBody EventReq eventReq) {
		Event savedEvent = eventRepository.save(new Event(eventReq));

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedEvent.getId())
				.toUri();

		// don't multi cast here because client does not know eventId yet

		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{eventId}")
	public ResponseEntity<?> get(@PathVariable String eventId) {
		return scroogeService.makeScroogeReq(eventId)
				.map(r -> ResponseEntity.ok().body(r))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/{eventId}/scrooges")
	public ResponseEntity<?> postScrooge(
			@Valid @RequestBody ScroogeReq scroogeReq,
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

		simpleWebSocketHandler.multiCastByEventId(eventId);

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
			simpleWebSocketHandler.multiCastByEventId(eventId);
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
			simpleWebSocketHandler.multiCastByEventId(eventId);
			return ResponseEntity.noContent().build();
		}
	}

	@DeleteMapping("/{eventId}/scrooges")
	public ResponseEntity<?> deleteByNameScrooge(
			@PathVariable String eventId,
			@RequestParam List<String> memberNames) {

		List<Scrooge> scrooges = memberNames.stream()
				.map(memberName -> scroogeRepository.findByEventIdAndMemberName(eventId, memberName))
				.flatMap(list -> list.stream())
				.collect(Collectors.toList());

		if (scrooges.isEmpty()) {
			return ResponseEntity.notFound().build();

		} else {
			scrooges.stream()
					.forEach(scroogeRepository::delete);

			simpleWebSocketHandler.multiCastByEventId(eventId);
			return ResponseEntity.noContent().build();
		}
	}
}
