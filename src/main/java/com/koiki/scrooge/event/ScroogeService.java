package com.koiki.scrooge.event;

import com.koiki.scrooge.scrooge.ScroogeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sun.misc.Contended;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScroogeService {
	private final EventRepository eventRepository;
	private final ScroogeRepository scroogeRepository;
	private final TransferAmountCalc transferAmountCalc;

	public Optional<EventRes> makeScroogeReq(String eventId) {
		return eventRepository.findById(eventId)
				.map(event -> {
					EventRes eventRes = new EventRes(event);
					eventRes.setScrooges(scroogeRepository.findByEventId(event.getId()));
					eventRes.setTransferAmounts(transferAmountCalc.calculate(eventRes.getScrooges()));
					return eventRes;
				});
	}
}
