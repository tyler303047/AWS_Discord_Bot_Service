import * as cdk from 'aws-cdk-lib';
import * as os from 'os';
import { Construct } from "constructs";
import { Function, InlineCode, Runtime, Code } from "aws-cdk-lib/aws-lambda";
import * as path from 'path';
import { aws_lambda, DockerVolume } from "aws-cdk-lib";
import * as apigwv2 from '@aws-cdk/aws-apigatewayv2-alpha';
import { HttpLambdaIntegration } from '@aws-cdk/aws-apigatewayv2-integrations-alpha';
import { MyStackProps } from "./utils/MyStackProps";

export class MyLambdaStack extends cdk.Stack {
    constructor(scope: Construct, id: string, stageName: string, props?: MyStackProps) {
        super(scope, id, props);

        const buildVolume: DockerVolume = {
            containerPath: '/root/.m2/',
            hostPath: os.homedir() + '/.m2/',
        }

        const projectCode = Code.fromAsset(path.join(__dirname, '/../..'), {
                bundling: {
                    image: aws_lambda.Runtime.JAVA_11.bundlingImage,
                    command: [
                        '/bin/sh',
                        '-c',
                        'mvn clean package ' +
                        '&& cp /asset-input/target/AWS_Lambda_API_Gateway_Tutorial-shaded-1.0-SNAPSHOT.jar /asset-output/'
                    ],
                    environment: {
                        "privileged": "true"
                    },
                    volumes: [
                        buildVolume,
                    ]
                }
            });

        const orchestrationHandler = new Function(this, 'OrchestrationLambda', {
            runtime: Runtime.JAVA_11,
            handler: 'main.com.tyler.awsDiscordBot.OrchestrationLambdaHandler::handleRequest',
            code: projectCode,
            environment: {
                "PUBLIC_KEY": props!.environmentVariables!.public_key
            },
            timeout: cdk.Duration.seconds(10),
        });

        // const apiEntrance = new aws_apigatewayv2.
        const apiEntrance = new apigwv2.HttpApi(this, "awsDiscordBotEntrance", {
            apiName: "AWS_Lambda_API Entrance",
            description: "Interactions endpoint for integration with discord to service requests for my discord bot.",
            defaultIntegration: new HttpLambdaIntegration('entranceIntegration', orchestrationHandler)
        });
    }
}