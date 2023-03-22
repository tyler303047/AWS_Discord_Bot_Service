#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { ProjectCdkPipeline } from '../lib/project_cdk-pipeline';

const app = new cdk.App();
new ProjectCdkPipeline(app, 'ProjectCdkPipeline', {
  env: {
      account: '801301537131',
      region: 'us-east-1',
  }
});

app.synth();