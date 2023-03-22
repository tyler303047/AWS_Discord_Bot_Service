import * as cdk from 'aws-cdk-lib';
import * as os from 'os';
import { Construct } from "constructs";
import { Function, InlineCode, Runtime, Code } from "aws-cdk-lib/aws-lambda";
import * as path from 'path';
import {aws_lambda, DockerVolume} from "aws-cdk-lib";

export class MyLambdaStack extends cdk.Stack {
    constructor(scope: Construct, id: string, stageName: string, props?: cdk.StackProps) {
        super(scope, id, props);

        const buildVolume: DockerVolume = {
            containerPath: '/root/.m2/',
            hostPath: os.homedir() + '/.m2/',
        }

        new Function(this, 'OrchestrationLambda', {
            runtime: Runtime.JAVA_11,
            handler: 'main.handler',
            code: Code.fromAsset(path.join(__dirname, '/../src/main/kotlin'), {
                bundling: {
                    image: aws_lambda.Runtime.JAVA_11.bundlingImage,
                    command: [
                        '/bin/sh',
                        '-c',
                        'cd com/tyler/awsDiscordBot/orchestration ',
                        '&& mvn clean install ',
                        '&& cp /asset-input/com/tyler/awsDiscordBot/orchestration/target/orchestration.jar /asset-output/'
                    ],
                    volumes: [
                        buildVolume,
                    ]
                }
            }),
        })
    }
}