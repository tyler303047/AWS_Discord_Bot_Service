import * as cdk from 'aws-cdk-lib';
import * as os from 'os';
import { Construct } from "constructs";
import { Function, InlineCode, Runtime, Code } from "aws-cdk-lib/aws-lambda";
import * as path from 'path';
import {aws_apigateway, aws_lambda, DockerVolume} from "aws-cdk-lib";

export class MyLambdaStack extends cdk.Stack {
    constructor(scope: Construct, id: string, stageName: string, props?: cdk.StackProps) {
        super(scope, id, props);

        const buildVolume: DockerVolume = {
            containerPath: '/root/.m2/',
            hostPath: os.homedir() + '/.m2/',
        }

        const orchestrationHandler = new Function(this, 'OrchestrationLambda', {
            runtime: Runtime.JAVA_11,
            handler: 'OrchestrationHandler.OrchestrationLambdaHandler::handleRequest',
            code: Code.fromAsset(path.join(__dirname, '/../../src/main/kotlin'), {
                bundling: {
                    image: aws_lambda.Runtime.JAVA_11.bundlingImage,
                    command: [
                        '/bin/sh',
                        '-c',
                        'cd com/tyler/awsDiscordBot/orchestration/ ' +
                        '&& mvn clean install ' +
                        '&& cd ../../../../ ' +
                        '&& cp /asset-input/com/tyler/awsDiscordBot/orchestration/target/orchestration-lambda-1.0.jar /asset-output/'
                    ],
                    environment: {
                        "privileged": "true"
                    },
                    volumes: [
                        buildVolume,
                    ]
                }
            }),
        });

        const apiEntrance = new aws_apigateway.RestApi(this, "awsDiscordBotEntrance", {
            restApiName: "AWS_Lambda_API Entrance",
            description: "Interactions endpoint for integration with discord to service requests for my discord bot."
        });

        const orchestrationIntegration = new aws_apigateway.LambdaIntegration(orchestrationHandler, {
            requestTemplates: { "application/json": '{ "statusCode": "200" }'}
        });

        apiEntrance.root.addMethod("GET", orchestrationIntegration);
    }
}