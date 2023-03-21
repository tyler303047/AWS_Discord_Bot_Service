import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import main.com.tyler.awsDiscordBot.OrchestrationLambdaHandler
import main.model.request.Request
import spock.lang.Specification

class MainTest extends Specification {

    def objectMapper = new ObjectMapper().registerModule(new KotlinModule())

    def "Main handler happy path"() {

        when: "The basic call happy path is enacted"
        def file = new File(filePath)
        def contents = file.readBytes()
        def event = objectMapper.readValue(contents, new TypeReference<Request>() {})
        def context = Mock(Context)
        def output = new OrchestrationLambdaHandler().handleRequest(event, context)

        then:
        output.statusCode == statusCode

        where:
        filePath                                        | statusCode
        "./test/resources/DiscordDefaultRequest.json"   | 200
        "./test/resources/SecondDiscordRequest.json"    | 401
    }
}
