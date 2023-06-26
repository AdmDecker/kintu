package kintu

import com.jayway.jsonpath.JsonPath
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.MAX_BLOCK_MS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*


class KintuProcessor( val eventClient: EventBrokerClient): KintuFileProcessor {
    override fun processFile(config: Config, kintuFile: KintuFile) {
        val json = if (kintuFile.randomize != null) {
            randomizeJsonPaths(kintuFile, kintuFile.randomize)
        }
        else kintuFile.payload.toString()

        eventClient.sendMessage(config, kintuFile.topic, json)
    }

    private fun randomizeJsonPaths(kintuFile: KintuFile, randomize: List<String>): String {
        var json = JsonPath.parse(kintuFile.payload.toString())
        for (jsonPath in randomize) {
            json = json.set(jsonPath, UUID.randomUUID().toString().replace("-", ""))
        }
        return json.jsonString()
    }

}

interface EventBrokerClient {
    fun sendMessage(config: Config, topic: String, payload: String)
}

class KafkaClient : EventBrokerClient {
    override fun sendMessage(config: Config, topic: String, payload: String) {
        val defaultProps = mapOf(
            ACKS_CONFIG to "all",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.qualifiedName,
            VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.qualifiedName,
            MAX_BLOCK_MS_CONFIG to 300
        )

        val props = defaultProps + config.kafka
        KafkaProducer<String, String>(props).use {
            it.send(
                ProducerRecord(topic, payload)
            ) { m: RecordMetadata, e: Exception? ->
                when (e) {
                    null -> println("Produced record to topic ${m.topic()} partition [${m.partition()}] @ offset ${m.offset()}")
                    else -> e.printStackTrace()
                }
            }
        }
    }
}