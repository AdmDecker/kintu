package kintu

import com.jayway.jsonpath.JsonPath
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*


@Singleton
class KintuProcessor( @Inject val eventClient: EventBrokerClient): KintuFileProcessor {
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

@Singleton
class KafkaClient : EventBrokerClient {
    override fun sendMessage(config: Config, topic: String, payload: String) {
        val properties = mapOf(
            ACKS_CONFIG to "all",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.qualifiedName,
            VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.qualifiedName,
            BOOTSTRAP_SERVERS_CONFIG to config.kafkaConfig.servers,
            MAX_BLOCK_MS_CONFIG to 300
        )
        KafkaProducer<String, String>(properties).use {
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