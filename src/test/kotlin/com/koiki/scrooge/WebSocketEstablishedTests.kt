package com.koiki.scrooge

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValues
import com.koiki.scrooge.event.EventReq
import com.koiki.scrooge.event.EventRes
import com.koiki.scrooge.scrooge.Scrooge
import com.koiki.scrooge.scrooge.ScroogeReq
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.client.getForObject
import org.assertj.core.api.Assertions.assertThat;
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.StandardWebSocketClient
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
import java.net.URI
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDateTime
import java.util.regex.Pattern

/**
 * https://github.com/bclozel/webflux-workshop/blob/master/trading-service/src/test/java/io/spring/workshop/tradingservice/websocket/EchoWebSocketHandlerTests.java
 */
@Slf4j
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketEstablishedTests(
        @LocalServerPort port: Int,
        @Autowired builder: RestTemplateBuilder
) {
    companion object{
        private val log = LoggerFactory.getLogger(WebSocketEstablishedTests::class.java)
    }

    private val restTemplate = builder
            .rootUri("http://localhost:$port")
            .build()

    val webSocketUri = "ws://localhost:$port/?"

    var id: String = ""

    val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    val expectedWsMessage = objectMapper.readValue("""
        {
            "name": "Koiki Camp",
            "id": "",
            "createdAt": "2017-12-02T16:52:45.52",
            "updatedAt": "2017-12-02T16:52:45.52",
            "scrooges": [
                {
                    "memberName": "Nabnab",
                    "paidAmount": 200,
                    "forWhat": "rent-a-car",
                    "id": "",
                    "eventId": "",
                    "createdAt": "2017-12-02T16:52:45.52",
                    "updatedAt": "2017-12-02T16:52:45.52"
                },
                {
                    "memberName": "Ninja",
                    "paidAmount": 500,
                    "forWhat": "beef",
                    "id": "",
                    "eventId": "",
                    "createdAt": "2017-12-02T16:52:45.52",
                    "updatedAt": "2017-12-02T16:52:45.52"
                }
            ],
            "aggPaidAmount": []
        }
    """, EventRes::class.java)

    fun postEvent() {
        val event1 = """
            {
                "name": "Koiki Camp"
            }
        """

        val scrooge1 = """
            {
                "memberName": "Nabnab",
                "paidAmount": 200,
                "forWhat": "rent-a-car"
            }
        """

        val scrooge2 = """
            {
                "memberName": "Ninja",
                "paidAmount": 500,
                "forWhat": "beef"
            }
        """

        val objectMapper = ObjectMapper()
        val eventReq: EventReq = objectMapper.readValue(event1, EventReq::class.java)
        val scroogeReq1: ScroogeReq = objectMapper.readValue(scrooge1, ScroogeReq::class.java)
        val scroogeReq2: ScroogeReq = objectMapper.readValue(scrooge2, ScroogeReq::class.java)

        val location = restTemplate.postForLocation("/events", eventReq)
        log.info("location: $location")

        val aaa = location.toString().split(Pattern.compile("/"))

        //val p = Pattern.compile("(\\d+)$")
        //val group = p.matcher(location.toString()).find()
        id = aaa[aaa.size-1]

        log.info("resource ID: $id")

        restTemplate.postForLocation("/events/$id/scrooges", scroogeReq1)
        restTemplate.postForLocation("/events/$id/scrooges", scroogeReq2)
    }

    @Test
    fun test() {
        this.postEvent()
        //id = "12345"

        val output: ReplayProcessor<String> = ReplayProcessor.create(5)
        val input = Flux.range(1, 2).map { index -> "msg-" + index!! }

        val webSocketClient = StandardWebSocketClient()

        val mono: Mono<Void> = webSocketClient.execute(URI(webSocketUri + id), object : WebSocketHandler {
            override fun handle(session: WebSocketSession) : Mono<Void> {
                val mono2: Mono<Void> = session
                        .send(input.map(session::textMessage))
                        // take(1) ... probably, # of receiving response of websocket
                        .thenMany(session.receive().take(1).map(WebSocketMessage::getPayloadAsText))
                        .subscribeWith(output)
                        .then()
                return mono2;
            }
        })

        mono.block(Duration.ofMillis(5000))

        val result = output.collectList().block(Duration.ofMillis(5000)).get(0)

        log.info("result: $result")



        val websocketResult: EventRes = objectMapper.readValue(result, EventRes::class.java)

        //assertThat(result.get("name")).isEqualTo("Koiki Camp")

        /*
        assertThat(websocketResult)
                .usingComparatorForFields(object : Comparator<String> {
                    override fun compare(a: String, b: String): Int {
                        return 0
                    }
                }, "id")
                .usingComparatorForFields(object : Comparator<LocalDateTime> {
                    override fun compare(a: LocalDateTime, b: LocalDateTime): Int {
                        return 0
                    }
                }, "createdAt", "updatedAt")
                .usingComparatorForFields(object : Comparator<List<Scrooge>> {
                    override fun compare(a: List<Scrooge>, b: List<Scrooge>): Int {
                        return 0
                    }
                }, "scrooges")
                .isEqualToComparingFieldByField(expectedWsMessage)
                */
        assertThat(websocketResult.createdAt)
                .isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(websocketResult.updatedAt)
                .isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(websocketResult.id)
                .isNotNull()
        assertThat(websocketResult)
                .isEqualToIgnoringGivenFields(expectedWsMessage,
                        "id", "scrooges", "createdAt", "updatedAt")

        assertThat(websocketResult.scrooges)
                .usingElementComparatorIgnoringFields("id", "eventId", "createdAt", "updatedAt")
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