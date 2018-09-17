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
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.StandardWebSocketClient
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
import reactor.core.publisher.toMono
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime
import java.util.regex.Pattern

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventControllerTest(
        @LocalServerPort port: Int,
        @Autowired builder: RestTemplateBuilder
) {
    companion object{
        private val log = LoggerFactory.getLogger(EventControllerTest::class.java)
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
            "transferAmounts": [
                {
                    "from": "Nabnab",
                    "to": "Ninja",
                    "amount": 150
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
    fun postEventAndScrooges_getEvent_success() {
        val eventLocation = restTemplate.postForLocation("/events", eventReq1)

        val compiledUri = eventLocation.toString().split(Pattern.compile("/"))
        val id = compiledUri[compiledUri.size - 1]

        val scrooge1Location = restTemplate.postForLocation("/events/$id/scrooges", scroogeReq1)
        restTemplate.postForLocation("/events/$id/scrooges", scroogeReq2)

        val event = restTemplate.getForObject(eventLocation!!, EventRes::class.java)
        assertThat(event?.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(event?.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(event?.id).isNotNull()
        assertThat(event)
                .isEqualToIgnoringGivenFields(
                        expectedWsMessage,
                        "id",
                        "scrooges", "createdAt", "updatedAt")

        assertThat(event?.scrooges)
                .usingElementComparatorIgnoringFields("id",
                        "eventId", "createdAt", "updatedAt")
                .isEqualTo(expectedWsMessage.scrooges)

        event?.scrooges?.stream()?.forEach({
            s ->
            assertThat(s.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
            assertThat(s.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
            assertThat(s.id).isNotNull()
            assertThat(s.eventId).isNotNull()
        })

        val scrooge1 = restTemplate.getForObject(scrooge1Location!!, Scrooge::class.java)
        assertThat(scrooge1).isNotNull()
    }

    @Test
    fun postEvent_getEvent_success() {
        val eventLocation = restTemplate.postForLocation("/events", eventReq1)

        val event = restTemplate.getForObject(eventLocation!!, EventRes::class.java)
        assertThat(event?.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(event?.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(event?.id).isNotNull()
        assertThat(event)
                .isEqualToIgnoringGivenFields(
                        objectMapper.readValue("""
                        {
                            "name": "Koiki Camp",
                            "id": "5a226c2d7c245e14f33fc5a8",
                            "createdAt": "2017-12-02T16:52:45.52",
                            "updatedAt": "2017-12-02T16:52:45.52",
                            "scrooges": [ ],
                            "transferAmounts": [ ],
                            "aggPaidAmount": [ ]
                        }
                    """, EventRes::class.java),
                        "id",
                        "createdAt", "updatedAt")
    }

    @Test
    fun deleteScrooge_success() {
        val eventLocation = restTemplate.postForLocation("/events", eventReq1)

        val compiledUri = eventLocation.toString().split(Pattern.compile("/"))
        val id = compiledUri[compiledUri.size - 1]

        val scroogeLocation = restTemplate.postForLocation("/events/$id/scrooges", scroogeReq1)

        restTemplate.getForObject(scroogeLocation!!, Scrooge::class.java)
        restTemplate.delete(scroogeLocation)
        try {
            restTemplate.getForObject(scroogeLocation, Scrooge::class.java)
        } catch (e: HttpClientErrorException) {
            assertThat(e.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }
    }

    @Test
    fun deleteScroogeBulk_success() {
        val eventLocation = restTemplate.postForLocation("/events", eventReq1)

        val compiledUri = eventLocation.toString().split(Pattern.compile("/"))
        val id = compiledUri[compiledUri.size - 1]

        val scroogeNab1Location = restTemplate.postForLocation("/events/$id/scrooges",
                objectMapper.readValue("""
                    {
                        "memberName": "Nabnab",
                        "paidAmount": 200,
                        "forWhat": "xxx"
                    }
                """, ScroogeReq::class.java))

        val scroogeNab2Location = restTemplate.postForLocation("/events/$id/scrooges",
                objectMapper.readValue("""
                    {
                        "memberName": "Nabnab",
                        "paidAmount": 400,
                        "forWhat": "xxx"
                    }
                """, ScroogeReq::class.java))

        val scroogeNinja1Location = restTemplate.postForLocation("/events/$id/scrooges",
                objectMapper.readValue("""
                    {
                        "memberName": "Ninja",
                        "paidAmount": 300,
                        "forWhat": "xxx"
                    }
                """, ScroogeReq::class.java))

        val scroogeF1Location = restTemplate.postForLocation("/events/$id/scrooges",
                objectMapper.readValue("""
                    {
                        "memberName": "F",
                        "paidAmount": 800,
                        "forWhat": "xxx"
                    }
                """, ScroogeReq::class.java))

        restTemplate.getForObject(scroogeNab1Location!!, Scrooge::class.java)
        restTemplate.getForObject(scroogeNab2Location!!, Scrooge::class.java)
        restTemplate.getForObject(scroogeNinja1Location!!, Scrooge::class.java)
        restTemplate.getForObject(scroogeF1Location!!, Scrooge::class.java)

        restTemplate.delete("/events/$id/scrooges?memberNames=Nabnab,Ninja")

        try {
            restTemplate.getForObject(scroogeNab1Location, Scrooge::class.java)
        } catch (e: HttpClientErrorException) {
            assertThat(e.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        try {
            restTemplate.getForObject(scroogeNab2Location, Scrooge::class.java)
        } catch (e: HttpClientErrorException) {
            assertThat(e.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        try {
            restTemplate.getForObject(scroogeNinja1Location, Scrooge::class.java)
        } catch (e: HttpClientErrorException) {
            assertThat(e.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        restTemplate.getForObject(scroogeF1Location, Scrooge::class.java)
    }

    //@Test
    fun receivingMultiCastTest() {
        val location = restTemplate.postForLocation("/events", eventReq1)

        val compiledUri = location.toString().split(Pattern.compile("/"))
        val scroogeId = compiledUri[compiledUri.size - 1]

        val numberOfReceivedMessages: Long = 2

        val output: ReplayProcessor<String> = ReplayProcessor.create(numberOfReceivedMessages.toInt())

        val mono = StandardWebSocketClient().execute(URI(webSocketUri + scroogeId)) {session ->
            log.info("session start!")
            val mono: Mono<Void> = session
                    .toMono()
                    .thenMany(session.receive().take(numberOfReceivedMessages)
                            .map(WebSocketMessage::getPayloadAsText))
                    .subscribeWith(output)
                    .then()
            log.info("session end!")
            mono
        }

        log.info("1st blocking")
        mono.block(Duration.ofMillis(5000))



        log.info("before 1st fetching websocket response")
        val websocketResult1: EventRes = objectMapper.readValue(
                output.collectList().block(Duration.ofMillis(5000))?.get(0),
                EventRes::class.java)
        log.info("after 1st fetching websocket response")

        assertThat(websocketResult1.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(websocketResult1.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(websocketResult1.id).isNotNull()
        assertThat(websocketResult1)
                .isEqualToIgnoringGivenFields(
                        objectMapper.readValue("""
                            {
                                "name": "Koiki Camp",
                                "id": "5a226c2d7c245e14f33fc5a8",
                                "createdAt": "2017-12-02T16:52:45.52",
                                "updatedAt": "2017-12-02T16:52:45.52",
                                "transferAmounts": [],
                                "aggPaidAmount": [],
                                "scrooges": []
                            }
                        """, EventRes::class.java),
                        "id",
                        "createdAt", "updatedAt")


        log.info("before posting scrooge")
        restTemplate.postForLocation("/events/$scroogeId/scrooges", scroogeReq1)

        val websocketResult2: EventRes = objectMapper.readValue(
                output.collectList().block(Duration.ofMillis(5000))?.get(1),
                EventRes::class.java)

        assertThat(websocketResult2.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(websocketResult2.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(websocketResult2.id).isNotNull()
        assertThat(websocketResult2)
                .isEqualToIgnoringGivenFields(
                        objectMapper.readValue("""
                            {
                                "name": "Koiki Camp",
                                "id": "5a226c2d7c245e14f33fc5a8",
                                "createdAt": "2017-12-02T16:52:45.52",
                                "updatedAt": "2017-12-02T16:52:45.52",
                                "transferAmounts": [],
                                "aggPaidAmount": [],
                                "scrooges": [
                                    {
                                        "memberName": "Nabnab",
                                        "paidAmount": 200,
                                        "forWhat": "rent-a-car",
                                        "id": "5a226c2d7c245e14f33fc5a8",
                                        "eventId": "5a226c2d7c245e14f33fc5a8",
                                        "createdAt": "2017-12-02T16:52:45.52",
                                        "updatedAt": "2017-12-02T16:52:45.52"
                                    }
                                ]
                            }
                        """, EventRes::class.java),
                        "id",
                        "createdAt", "updatedAt")

        assertThat(websocketResult2.scrooges)
                .usingElementComparatorIgnoringFields("id",
                        "eventId", "createdAt", "updatedAt")
                .isEqualTo(objectMapper.readValue("""
                        {
                            "memberName": "Nabnab",
                            "paidAmount": 200,
                            "forWhat": "rent-a-car"
                        }
                    """, ScroogeReq::class.java))

        websocketResult2.scrooges.stream().forEach({
            s ->
            assertThat(s.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
            assertThat(s.updatedAt).isBeforeOrEqualTo(LocalDateTime.now())
            assertThat(s.id).isNotNull()
            assertThat(s.eventId).isNotNull()
        })

        log.info("end ot UT")
    }
}