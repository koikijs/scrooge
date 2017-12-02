package com.koiki.scrooge

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.koiki.scrooge.event.EventReq
import com.koiki.scrooge.event.EventRes
import com.koiki.scrooge.scrooge.Scrooge
import com.koiki.scrooge.scrooge.ScroogeReq
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.StandardWebSocketClient
import reactor.core.publisher.ReplayProcessor
import reactor.core.publisher.toMono
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime
import java.util.regex.Pattern

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventControllerTests(
        @LocalServerPort port: Int,
        @Autowired builder: RestTemplateBuilder
) {
    companion object{
        private val log = LoggerFactory.getLogger(EventControllerTests::class.java)
    }

    private val restTemplate = builder
            .rootUri("http://localhost:$port")
            .build()

    private val webSocketUri = "ws://localhost:$port/?"

    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    private val expectedWsMessage = objectMapper.readValue("""
        {
            "name": "Koiki Camp",
            "id": "5a226c2d7c245e14f33fc5a8",
            "createdAt": "2017-12-02T16:52:45.52",
            "updatedAt": "2017-12-02T16:52:45.52",
            "scrooges": [
                {
                    "memberName": "Nabnab",
                    "paidAmount": 200,
                    "forWhat": "rent-a-car",
                    "id": "5a226c2d7c245e14f33fc5a8",
                    "eventId": "5a226c2d7c245e14f33fc5a8",
                    "createdAt": "2017-12-02T16:52:45.52",
                    "updatedAt": "2017-12-02T16:52:45.52"
                },
                {
                    "memberName": "Ninja",
                    "paidAmount": 500,
                    "forWhat": "beef",
                    "id": "5a226c2d7c245e14f33fc5a8",
                    "eventId": "5a226c2d7c245e14f33fc5a8",
                    "createdAt": "2017-12-02T16:52:45.52",
                    "updatedAt": "2017-12-02T16:52:45.52"
                }
            ],
            "aggPaidAmount": []
        }
    """, EventRes::class.java)

    private val eventReq1 = objectMapper.readValue("""
            {
                "name": "Koiki Camp"
            }
        """, EventReq::class.java)

    private val scroogeReq1 = objectMapper.readValue("""
            {
                "memberName": "Nabnab",
                "paidAmount": 200,
                "forWhat": "rent-a-car"
            }
        """, ScroogeReq::class.java)

    private val scroogeReq2 = objectMapper.readValue("""
            {
                "memberName": "Ninja",
                "paidAmount": 500,
                "forWhat": "beef"
            }
        """, ScroogeReq::class.java)

    private val prepare: String
        get() {
            val location = restTemplate.postForLocation("/events", eventReq1)

            val compiledUri = location.toString().split(Pattern.compile("/"))
            val id = compiledUri[compiledUri.size - 1]

            restTemplate.postForLocation("/events/$id/scrooges", scroogeReq1)
            restTemplate.postForLocation("/events/$id/scrooges", scroogeReq2)

            return id
        }

    @Test
    fun postEventAndScrooges_getEvent() {
        val eventLocation = restTemplate.postForLocation("/events", eventReq1)

        val compiledUri = eventLocation.toString().split(Pattern.compile("/"))
        val id = compiledUri[compiledUri.size - 1]

        var scrooge1Location = restTemplate.postForLocation("/events/$id/scrooges", scroogeReq1)
        restTemplate.postForLocation("/events/$id/scrooges", scroogeReq2)

        val event = restTemplate.getForObject(eventLocation, EventRes::class.java)
        assertThat(event.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(event.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(event.id).isNotNull()
        assertThat(event)
                .isEqualToIgnoringGivenFields(
                        expectedWsMessage,
                        "id",
                        "scrooges", "createdAt", "updatedAt")

        assertThat(event.scrooges)
                .usingElementComparatorIgnoringFields("id",
                        "eventId", "createdAt", "updatedAt")
                .isEqualTo(expectedWsMessage.scrooges)

        event.scrooges.stream().forEach({
            s ->
            assertThat(s.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
            assertThat(s.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
            assertThat(s.id).isNotNull()
            assertThat(s.eventId).isNotNull()
        })

        val scrooge1 = restTemplate.getForObject(scrooge1Location, Scrooge::class.java)
        assertThat(scrooge1).isNotNull()
    }


    //@Test
    fun test() {
        val scroogeId = this.prepare

        val numberOfReceivedMessages: Long = 1

        val output: ReplayProcessor<String> = ReplayProcessor.create(numberOfReceivedMessages.toInt())

        StandardWebSocketClient().execute(URI(webSocketUri + scroogeId)) { session ->
            session
                    .toMono()
                    .thenMany(session.receive().take(numberOfReceivedMessages)
                            .map(WebSocketMessage::getPayloadAsText))
                    .subscribeWith(output)
                    .then()
        }.block(Duration.ofMillis(5000))

        val websocketResult: EventRes = objectMapper.readValue(
                output.collectList().block().get(0),
                EventRes::class.java)

        assertThat(websocketResult.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(websocketResult.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(websocketResult.id).isNotNull()
        assertThat(websocketResult)
                .isEqualToIgnoringGivenFields(
                        expectedWsMessage,
                        "id",
                        "scrooges", "createdAt", "updatedAt")

        assertThat(websocketResult.scrooges)
                .usingElementComparatorIgnoringFields("id",
                        "eventId", "createdAt", "updatedAt")
                .isEqualTo(expectedWsMessage.scrooges)

        websocketResult.scrooges.stream().forEach({
            s ->
            assertThat(s.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
            assertThat(s.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
            assertThat(s.id).isNotNull()
            assertThat(s.eventId).isNotNull()
        })
    }
}