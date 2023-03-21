import * as cdk from 'aws-cdk-lib';
import { Construct } from "constructs";
import { Function, InlineCode, Runtime, Code } from "aws-cdk-lib/aws-lambda";
import * as path from 'path';
import {aws_lambda} from "aws-cdk-lib";

export class MyLambdaStack extends cdk.Stack {
    constructor(scope: Construct, id: string, stageName: string, props?: cdk.StackProps) {
        super(scope, id, props);
        new Function(this, 'OrchestrationLambda', {
            runtime: Runtime.JAVA_11,
            handler: 'main.handler',
            code: Code.fromAsset(path.join(__dirname, '../src/main/kotlin'), {
                bundling: {
                    image: aws_lambda.Runtime.JAVA_11.bundlingImage,
                    command: [
                        ''
                    ]
                }
            }),
        })
    }
}