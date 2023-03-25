import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import {CodePipeline, CodePipelineSource, ManualApprovalStep, ShellStep} from 'aws-cdk-lib/pipelines';
import {MyPipelineAppStage} from "./project_cdk-app-stage";

export class ProjectCdkPipeline extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const pipeline = new CodePipeline(this, 'Pipeline', {
      pipelineName: 'MyPipeline',
      synth: new ShellStep('Synth', {
          input: CodePipelineSource.connection('tyler303047/AWS_Discord_Bot_Service', 'main', {
              connectionArn: 'arn:aws:codestar-connections:us-east-1:801301537131:connection/55337186-87da-460c-8251-d2f386c4b6c3',
          }),
          installCommands: [
              'npm install -g aws-cdk@2.70.0'
          ],
          commands: [
            'cd project_cdk',
            'npm ci',
            'npm run build',
            'npx cdk synth',
          ],
          primaryOutputDirectory: 'project_cdk/cdk.out',
      }),
        codeBuildDefaults: {
          buildEnvironment: {
              privileged: true,
          },
        },
    });

    const testingStage = pipeline.addStage(new MyPipelineAppStage(this, "test", {
        env: { account: "801301537131", region: "us-east-2" },
        environmentVariables: {
            public_key: "96376e878b51e7436d1a918900d30b67db05d4157828550fcb65a1cbfd40ecee"
        }
    }));

    // testingStage.addPost(new ManualApprovalStep('Manual approval step before deployment to production'));

    const prodStage = pipeline.addStage(new MyPipelineAppStage(this, "prod", {
        env: { account: "801301537131", region: "us-east-1" },
        environmentVariables: {
            public_key: "96376e878b51e7436d1a918900d30b67db05d4157828550fcb65a1cbfd40ecee"
        }
    }))

    prodStage.addPre(new ManualApprovalStep('Manual approval step before deployment to production'));
  }
}
