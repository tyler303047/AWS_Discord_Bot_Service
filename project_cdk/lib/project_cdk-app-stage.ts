import * as cdk from 'aws-cdk-lib';
import { Construct } from "constructs";
import { MyLambdaStack } from "./project_cdk-lambda-stack";
import { MyStackProps } from './utils/MyStackProps';

export class MyPipelineAppStage extends cdk.Stage {

    constructor(scope: Construct, stageName: string, props?: MyStackProps) {
        super(scope, stageName, props);

        const stack = new MyLambdaStack(this, 'LambdaStack', stageName, props);
    }

}