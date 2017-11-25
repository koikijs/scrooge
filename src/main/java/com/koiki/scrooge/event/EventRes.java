package com.koiki.scrooge.event;

import com.koiki.scrooge.scrooge.Scrooge;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
public class EventRes extends Event {
	private List<Scrooge> scrooges;

}
