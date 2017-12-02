package com.koiki.scrooge.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.koiki.scrooge.scrooge.Scrooge
import org.junit.jupiter.api.Test
import java.util.*

internal class TransferAmountCalcTest {
    val transferAmountCalc = TransferAmountCalc()
    val objectMapper = ObjectMapper()

    @Test
    fun test() {
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
        transferAmountCalc.calculate(scrooges);
    }
}