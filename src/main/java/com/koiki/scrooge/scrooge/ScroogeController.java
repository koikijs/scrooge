package com.koiki.scrooge.scrooge;

import com.koiki.scrooge.event.Event;
import com.koiki.scrooge.event.EventRepository;
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
@RequestMapping("/scrooges")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScroogeController {
	private final ScroogeRepository scroogeRepository;

	@GetMapping("/{scroogeId}")
	public ResponseEntity<?> get(@PathVariable String scroogeId) {
		return scroogeRepository.findById(scroogeId)
				.map(event -> {
					return ResponseEntity.ok().body(event);
				})
				.orElse(ResponseEntity.notFound().build());
	}
}
