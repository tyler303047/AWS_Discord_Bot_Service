import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CodePipeline, CodePipelineSource, ShellStep } from 'aws-cdk-lib/pipelines';
import {GitHubSourceAction} from "aws-cdk-lib/aws-codepipeline-actions";
// import * as sqs from 'aws-cdk-lib/aws-sqs';

export class ProjectCdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const pipeline = new CodePipeline(this, 'Pipeline', {
      pipelineName: 'MyPipeline',
      synth: new ShellStep('Synth', {
        input: CodePipelineSource.gitHub('tyler303047/AWS_Discord_Bot_Service', 'main', {
            authentication: cdk.SecretValue.secretsManager('github-token')
        }),
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
