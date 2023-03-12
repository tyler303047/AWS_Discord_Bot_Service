# AWS_Discord_Bot_Service

<center> <h2>This is a simple repository to use some more standardized software engineering practices to make a service backend for a discord bot using AWS</h3> </center>

The main specifications of this project to be implemented is as follows:
  1) A CDK to programatically define AWS resources in typescript
  2) A CI/CD pipeline to build, test, and deploy the source code into those AWS resources
  3) Source code written in Kotlin to define the decision-making and processing of the system
  4) Unit tests to test each lambda and its corresponding assumptions
  5) Integration tests to test the end-to-end computation from the user's perspective
  
  
  
  

![alt text](https://github.com/tyler303047/AWS_Discord_Bot_Service/blob/main/asset_files/Discord_Bot_Diagram.jpg?raw=true)
<center> <h3>The shows the full architecture of the service at the time of writing</h3> </center>

The idea behind this architecture is that the user will send a slash command through discord, which will go to an orchestration lambda. This orchestration lambda will decide which slash command it needs to execute, then send that to the corresponding lambda through an event context subscription in SNS. The add command handler will take in a link to raidbots.com, get the json file from that link, process it, and then upload the data from the processing to DynamoDB. The search user command handler will take in the `user_id` of the user in question and return all rows that match from DynamoDB. The search item command handler will take in the `item` field and return all matches from DynamoDB.
