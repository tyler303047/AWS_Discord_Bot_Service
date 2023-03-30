import * as cdk from 'aws-cdk-lib';
import * as os from 'os';
import { Construct } from "constructs";
import { Function, InlineCode, Runtime, Code } from "aws-cdk-lib/aws-lambda";
import * as path from 'path';
import {aws_lambda, aws_lambda_event_sources, aws_sns, DockerVolume} from "aws-cdk-lib";
import * as apigwv2 from '@aws-cdk/aws-apigatewayv2-alpha';
import { HttpLambdaIntegration } from '@aws-cdk/aws-apigatewayv2-integrations-alpha';
import { MyStackProps } from "./utils/MyStackProps";
import { Topic } from "aws-cdk-lib/aws-sns";
import {PolicyStatement} from "aws-cdk-lib/aws-iam";

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

        const orchestrationTopic = new Topic(this, 'OrchestrationTopic');

        const orchestrationPublishingPolicy = new PolicyStatement({
            actions: ['sns:publish'],
            resources: ['*'],
        });

        const orchestrationHandler = new Function(this, 'OrchestrationLambda', {
            runtime: Runtime.JAVA_11,
            handler: 'com.tyler.awsDiscordBot.orchestration.OrchestrationLambdaHandler::handleRequest',
            code: projectCode,
            environment: {
                "PUBLIC_KEY": props!.environmentVariables!.public_key,
                "SNS_ARN": orchestrationTopic.topicArn
            },
            timeout: cdk.Duration.seconds(10),
            memorySize: 512,
        });

        orchestrationHandler.addToRolePolicy(orchestrationPublishingPolicy);

        const apiEntrance = new apigwv2.HttpApi(this, "awsDiscordBotEndpoint", {
            apiName: "AWS_Lambda_API Entrance",
            description: "Interactions endpoint for integration with discord to service requests for my discord bot.",
            defaultIntegration: new HttpLambdaIntegration('entranceIntegration', orchestrationHandler)
        });

        const addHandler = new Function(this, 'AddHandler', {
            runtime: Runtime.JAVA_11,
            handler: 'com.tyler.awsDiscordBot.addCommand.AddCommandLambdaHandler::handleRequest',
            code: projectCode,
            timeout: cdk.Duration.seconds(10),
            memorySize: 512,
        });

        addHandler.addEventSource(new aws_lambda_event_sources.SnsEventSource(orchestrationTopic, {
            filterPolicy: {
                command_type: aws_sns.SubscriptionFilter.stringFilter({allowlist: ['add-command']})
            }
        }));

        /*
            Add snapstart support
         */
        (orchestrationHandler.node.defaultChild as aws_lambda.CfnFunction).addPropertyOverride('SnapStart', {
            ApplyOn: 'PublishedVersions',
        });
        (addHandler.node.defaultChild as aws_lambda.CfnFunction).addPropertyOverride('SnapStart', {
            ApplyOn: 'PublishedVersions',
        });

        new aws_lambda.Version(this, 'MyVersionOrchestration', {
            lambda: orchestrationHandler,
        });
        new aws_lambda.Version(this, 'MyVersionAdd', {
            lambda: addHandler,
        });
    }
}