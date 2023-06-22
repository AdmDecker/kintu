package kintu

import io.mockk.mockk
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Test

class KintuProcessorTest {
    @Test
    fun processorShouldDoSomething() {
        val config = Config("some-environment", "http://localhost:9092")
        val file = KintuFile("some-topic", JsonObject(
            mapOf(
                "test" to JsonPrimitive("something")
            )
        ))

        val processor = KintuProcessor(mockk(relaxed = true))
        processor.processFile(config, file)
    }
}