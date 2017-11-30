package com.koiki.scrooge

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.client.getForObject
import org.assertj.core.api.Assertions.*
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthTests(
        @LocalServerPort port: Int,
        @Autowired builder: RestTemplateBuilder
) {

    private val restTemplate = builder
            .rootUri("http://localhost:$port")
            .build()

    @Test
    fun test() {
        val expected = JSONObject("""
            {
                "status": "UP"
            }
        """)

        val actual = JSONObject(restTemplate.getForObject<String>("/application/status"))
        assertThat(actual).isEqualToComparingFieldByField(expected)
    }
}