package kintu

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer

class KintuProcessor: KintuFileProcessor {
    override fun processFile(config: Config, kintuFile: KintuFile) {
        val properties = mapOf(
            ACKS_CONFIG to "all",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.qualifiedName,
            VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.qualifiedName,
            BOOTSTRAP_SERVERS_CONFIG to "http://localhost:9092"
        )
        KafkaProducer<String, String>(properties).use {
            it.send(
                ProducerRecord(kintuFile.topic, kintuFile.payload)) { m: RecordMetadata, e: Exception? ->
                    when (e) {
                        null -> println("Produced record to topic ${m.topic()} partition [${m.partition()}] @ offset ${m.offset()}")
                        else -> e.printStackTrace()
                    }
                }
        }
    }
}