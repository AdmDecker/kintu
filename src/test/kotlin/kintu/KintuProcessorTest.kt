package kintu

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldNotContain
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Test

class KintuProcessorTest {
    private val capturedEventMessage = CapturingSlot<String>()
    private val eventBrokerClient: EventBrokerClient =
        mockk<EventBrokerClient>(relaxed = true)
    private var config: Config = Config("", KafkaConfig(""))
    private var kintuFile = KintuFile("", null, JsonObject(mapOf()))

    @Test
    fun processorShouldEmitEvent() {
        givenStandardConfig()
        givenStandardKintuFile()

        whenProcessFileExecuted()

        verify { eventBrokerClient.sendMessage(config, "some-topic", any()) }
        capturedEventMessage.captured shouldBeEqual """{"test":"something"}"""
    }

    @Test
    fun processorShouldEmitRandomizedValues() {
        givenStandardConfig()
        givenKintuFileWithRandomizer()

        whenProcessFileExecuted()

        verify { eventBrokerClient.sendMessage(config, "some-topic", any()) }
        capturedEventMessage.captured shouldNotContain "something"
    }

    private fun givenStandardConfig() {
        config = Config("some-environment", KafkaConfig("http://localhost:9092"))
    }

    private fun givenStandardKintuFile() {
        kintuFile = KintuFile(
            "some-topic", null, JsonObject(
                mapOf(
                    "test" to JsonPrimitive("something")
                )
            )
        )
    }

    private fun givenKintuFileWithRandomizer() {
        kintuFile = KintuFile(
            "some-topic", listOf("test"), JsonObject(
                mapOf(
                    "test" to JsonPrimitive("something")
                )
            )
        )
    }

    private fun whenProcessFileExecuted() {
        every {
            eventBrokerClient.sendMessage(any(), any(), capture(capturedEventMessage))
        } answers { nothing }
        val processor = KintuProcessor(eventBrokerClient)
        processor.processFile(config, kintuFile)
    }
}