import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.tyler.awsDiscordBot.orchestration.OrchestrationLambdaHandler
import main.model.request.Request
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import spock.lang.Specification

class OrchestrationHandlerTest extends Specification {

    def objectMapper = new ObjectMapper().registerModule(new KotlinModule())

    def snsClient = Mock(SnsClient)

    def "Main handler happy path"() {

        when: "The basic call happy path is enacted"
        def file = new File(filePath)
        def contents = file.readBytes()
        def event = objectMapper.readValue(contents, new TypeReference<Request>() {})
        def context = Mock(Context)
        def zeroArgHandler = new OrchestrationLambdaHandler()
        def output = new OrchestrationLambdaHandler(
                null,
                null,
                snsClient
        ).handleRequest(event, context)

        then:
        noExceptionThrown()
        output.statusCode == statusCode
        sendForwardNum * snsClient.publish(_ as PublishRequest)

        where:
        filePath                                                | statusCode    |   sendForwardNum
        "./src/test/resources/DiscordDefaultRequest.json"       | 200           |   0
        "./src/test/resources/DiscordPingCommandRequest.json"   | 200           |   0
        "./src/test/resources/SecondDiscordRequest.json"        | 401           |   0
    }
}
