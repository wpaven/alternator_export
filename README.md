# Scylla Alternator Export

ScyllaDB has a DynamoDB compatible API which is named Scylla Alternator. You can use the AWS CLI and SDK to access ScyllaDB. This is quite awesome.

DynamoDB has an export to S3 capability, but currently Scylla does not. Not so awesome.

This repo is me looking into how one might scan an Alternator table in Scylla and export to S3. This is very early stages and if Scylla Engineering adds this capability, this code will no longer be needed. But hopefully it can help serve in helping others to understand how to access Dynamo compatible data in Scylla.

