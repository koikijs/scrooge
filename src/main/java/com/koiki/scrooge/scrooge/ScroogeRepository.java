package com.koiki.scrooge.scrooge;

import com.koiki.scrooge.event.Event;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScroogeRepository extends MongoRepository<Scrooge, String> {
	public List<Scrooge> findByEventId(String eventId);
}
