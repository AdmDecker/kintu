package kintu

import org.junit.jupiter.api.Test

class KintuProcessorTest {
    @Test
    fun processorShouldDoSomething() {
        val config = Config("some-environment")
        val file = KintuFile("some-topic", "some-payload")

        val processor = KintuProcessor()
        processor.processFile(config, file)
    }
}