package kintu

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.string.beEmpty
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.FileSystem
import java.nio.file.Files

class KintuCommandTest {
    private lateinit var fakeFs: FileSystem
    private var err: StringWriter = StringWriter()
    private var out: StringWriter = StringWriter()
    private var capturedConfig = CapturingSlot<Config>()
    private var capturedKintuFile = CapturingSlot<KintuFile>()
    private val mockKintuProcessor = mockk<KintuFileProcessor>(relaxed = true)
    private var workingDirectory = "/home/work"

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        every {
            mockKintuProcessor.processFile(
                capture(capturedConfig),
                capture(capturedKintuFile))
        } answers { nothing }

        val configuration = Configuration.unix().toBuilder()
            .setWorkingDirectory(workingDirectory)
            .build()
        fakeFs = Jimfs.newFileSystem(configuration)
    }

    @Test
    fun testHappyPath() {
        givenTypicalConfig()
        givenTypicalTestFile()

        whenCommandExecuted()

        noErrorsShouldBeOutput()
        kintuProcessorShouldHaveBeenCalled()
    }

    @Test
    fun testFileInParentDirectory() {
        givenTypicalTestFileInParentDirectory()
        givenTypicalConfig()

        whenCommandExecuted()

        noErrorsShouldBeOutput()
        kintuProcessorShouldHaveBeenCalled()
    }

    @Test
    fun testConfigInParentDirectory() {
        givenTypicalConfigInParentDirectory()
        givenTypicalTestFile()

        whenCommandExecuted()

        noErrorsShouldBeOutput()
    }

    @Test
    fun testMissingConfig() {
        whenCommandExecuted()

        out.toString() shouldBeEqual "p"
    }

    private fun givenTypicalConfigInParentDirectory() {
        addTestFile("/home/kintu.conf", configFileContent)
    }

    private fun noErrorsShouldBeOutput() {
        err.toString() should beEmpty()
    }

    private fun givenTypicalConfig() {
        addTestFile("kintu.conf", configFileContent)
    }

    private fun kintuProcessorShouldHaveBeenCalled() {
        verify(exactly = 1) { mockKintuProcessor.processFile(any(), any()) }
        capturedKintuFile.captured.topic shouldBeEqual "testTopic"
        capturedConfig.captured.environment shouldBeEqual "myenv"
    }

    private fun givenTypicalTestFile() {
        addTestFile(
            "kintu.kintu",
            typicalFileContent
        )
    }

    private fun givenTypicalTestFileInParentDirectory() {
        addTestFile(
            "/home/kintu.kintu",
            typicalFileContent
        )
    }

    private fun whenCommandExecuted(): Int {
        val app = KintuCommand(fakeFs, mockKintuProcessor)
        val cmd = CommandLine(app)

        cmd.err = PrintWriter(err)
        cmd.out = PrintWriter(out)

        return cmd.execute("kintu")
    }

    private fun addTestFile(path: String, content: String) {
        val file = fakeFs.getPath(path)
        Files.createFile(file)
        Files.write(
            file,
            content.split("\r?\n|\r".toRegex()).toList()
        )
    }
}

private const val typicalFileContent: String =
    """
        { "topic": "testTopic" }
    """

private const val configFileContent = "environment=myenv"
