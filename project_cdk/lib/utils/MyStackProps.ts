import * as cdk from 'aws-cdk-lib';

export interface MyStackProps extends cdk.StackProps {
    environmentVariables: {
        public_key: string
    }
}