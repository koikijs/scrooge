package com.koiki.scrooge.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.koiki.scrooge.scrooge.Scrooge
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TransferAmountCalcTest {
    val transferAmountCalc = TransferAmountCalc()
    val objectMapper = ObjectMapper().registerModule(KotlinModule())

    @Test
    fun test_success() {
        val scrooges: List<Scrooge> = objectMapper.readValue("""
            [
                {
                    "memberName": "side",
                    "paidAmount": 9602
                },
                {
                    "memberName": "side",
                    "paidAmount": 644
                },
                {
                    "memberName": "f",
                    "paidAmount": 48500
                },
                {
                    "memberName": "sushi",
                    "paidAmount": 360
                },
                {
                    "memberName": "nab",
                    "paidAmount": 9648
                },
                {
                    "memberName": "ino",
                    "paidAmount": 6231
                },
                {
                    "memberName": "ninja",
                    "paidAmount": 26784
                },
                {
                    "memberName": "ninja",
                    "paidAmount": 17667
                },
                {
                    "memberName": "ninja",
                    "paidAmount": 1050
                },
                {
                    "memberName": "ninja",
                    "paidAmount": 3321
                },
                {
                    "memberName": "ninja",
                    "paidAmount": 5260
                }
            ]
        """)

        val expected: List<TransferAmount> = objectMapper.readValue("""
            [
                {
                    "from": "sushi",
                    "to": "ninja",
                    "amount": 21151
                },
                {
                    "from": "ino",
                    "to": "f",
                    "amount": 15280
                },
                {
                    "from": "nab",
                    "to": "f",
                    "amount": 11863
                },
                {
                    "from": "side",
                    "to": "ninja",
                    "amount": 11265
                },
                {
                    "from": "f",
                    "to": "ninja",
                    "amount": 154
                }
            ]
        """)
        val actual = transferAmountCalc.calculate(scrooges)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun test_success2() {
        val scrooges: List<Scrooge> = objectMapper.readValue(""" [] """)

        val expected: List<TransferAmount> = objectMapper.readValue(""" [] """)
        val actual = transferAmountCalc.calculate(scrooges)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun test_success3() {
        val scrooges: List<Scrooge> = objectMapper.readValue("""
            [
                {
                    "memberName": "side",
                    "paidAmount": 9602
                }
            ]
        """)

        val expected: List<TransferAmount> = objectMapper.readValue("""
            [ ]
        """)
        val actual = transferAmountCalc.calculate(scrooges)

        assertThat(actual).isEqualTo(expected)
    }
}