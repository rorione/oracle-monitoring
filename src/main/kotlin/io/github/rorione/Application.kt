package io.github.rorione

import com.typesafe.config.ConfigFactory
import io.github.rorione.abi.ExchangeRate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import org.web3j.abi.EventEncoder
import org.web3j.abi.EventValues
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.Event
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.websocket.WebSocketService
import org.web3j.protocol.websocket.events.Log
import org.web3j.protocol.websocket.events.LogNotification
import java.math.BigInteger

val logger: Logger = LoggerFactory.getLogger("main")

fun main() {
    val config = Config(ConfigFactory.load())
    val service = WebSocketService(config.nodeUrl, false).apply {
        connect()
    }
    val web3j = Admin.build(service)

    val topic = listOf(EventEncoder.encode(ExchangeRate.ANSWERUPDATED_EVENT))
    val oracles = config.oracles.map { (name, address) ->
        val marker: Marker = MarkerFactory.getMarker(name)

        web3j.logsNotifications(listOf(address), topic).subscribe { logNotification: LogNotification ->
            extractEventParameters(
                ExchangeRate.ANSWERUPDATED_EVENT,
                logNotification.params.result
            )?.let {
                val current = it.indexedValues[0].value as BigInteger
                val roundId = it.indexedValues[1].value as BigInteger
                val updatedAt = it.nonIndexedValues[0].value as BigInteger
                logger.info(marker, "ts: $updatedAt, current: $current, roundId: $roundId")
            } ?: logger.warn(marker, "Malformed event")
        }.also {
            logger.info(marker, "Oracle subscribed")
        }
    }

    val marker: Marker = MarkerFactory.getMarker("BLOCK")
    val blocks = web3j.newHeadsNotifications().subscribe { headNotification ->
        with(headNotification.params.result) {
            logger.info(marker, "ts: $timestamp, number: $number, hash: $hash")
        }
    }
    logger.info(marker, "Blocks subscribed")

    Runtime.getRuntime().addShutdownHook(Thread {
        while (oracles.any { !it.isDisposed }) {
            oracles.forEach { it.dispose() }
        }
        logger.info("All oracles unsubscribed")
        while (!blocks.isDisposed) {
            blocks.dispose()
        }
        logger.info(marker, "Blocks unsubscribed")
        web3j.shutdown()
        service.close()
    })
}


fun extractEventParameters(event: Event, log: Log): EventValues? {
    val topics = log.topics
    if (topics.isEmpty() || topics.first() != EventEncoder.encode(event)) {
        return null
    }
    val indexedValues = buildList {
        event.indexedParameters.forEachIndexed { index, typeReference ->
            val value = FunctionReturnDecoder.decodeIndexedValue(
                topics[index + 1], typeReference
            )
            add(value)
        }
    }
    val nonIndexedValues = FunctionReturnDecoder.decode(log.data, event.nonIndexedParameters)
    return EventValues(indexedValues, nonIndexedValues)
}
