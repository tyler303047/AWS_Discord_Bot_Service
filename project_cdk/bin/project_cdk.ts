#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { ProjectCdkStack } from '../lib/project_cdk-stack';

const app = new cdk.App();
new ProjectCdkStack(app, 'ProjectCdkStack', {
  env: {
      account: '801301537131',
      region: 'us-east-1',
  }
});

app.synth();