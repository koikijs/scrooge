package com.koiki.scrooge

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValues
import com.koiki.scrooge.event.EventReq
import com.koiki.scrooge.event.EventRes
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.client.getForObject
import org.assertj.core.api.Assertions.*
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
import java.util.regex.Pattern


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

    fun postEvent() {
        val event1 = """
            {
                "name": "Koiki Camp"
            }
        """
        val objectMapper = ObjectMapper()
        val eventReq: EventReq = objectMapper.readValue(event1, EventReq::class.java)

        val location = restTemplate.postForLocation("/events", eventReq)
        log.info("location: $location")

        val aaa = location.toString().split(Pattern.compile("/"))

        //val p = Pattern.compile("(\\d+)$")
        //val group = p.matcher(location.toString()).find()
        id = aaa[aaa.size-1]

        log.info("resource ID: $id")

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
                        .thenMany(session.receive().take(1).map(WebSocketMessage::getPayloadAsText))
                        .subscribeWith(output)
                        .then()
                return mono2;
            }
        })

        mono.block(Duration.ofMillis(5000))

        val result = output.collectList().block(Duration.ofMillis(5000)).get(0)

        log.info("result: $result")

        val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

        val websocketResult: EventRes = objectMapper.readValue(result, EventRes::class.java)

        assertThat(websocketResult.name).isEqualTo("Koiki Camp")
    }
}