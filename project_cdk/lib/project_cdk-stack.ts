import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CodePipeline, CodePipelineSource, ShellStep } from 'aws-cdk-lib/pipelines';
import {GitHubSourceAction} from "aws-cdk-lib/aws-codepipeline-actions";
import {SecretValue} from "aws-cdk-lib";
// import * as sqs from 'aws-cdk-lib/aws-sqs';

export class ProjectCdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const pipeline = new CodePipeline(this, 'Pipeline', {
      pipelineName: 'MyPipeline',
      synth: new ShellStep('Synth', {
          input: CodePipelineSource.connection('tyler303047/AWS_Discord_Bot_Service', 'main', {
              connectionArn: 'arn:aws:codestar-connections:us-east-1:801301537131:connection/55337186-87da-460c-8251-d2f386c4b6c3',
          }),
          installCommands: [
              'npm install -g aws-cdk'
          ],
          commands: [
            'cd project_cdk',
            'npm ci',
            'npm run build',
            'npx cdk synth'
          ]
      })
    });
  }
}
