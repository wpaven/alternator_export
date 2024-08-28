# Scylla Alternator Export

ScyllaDB has a DynamoDB compatible API which is named Scylla Alternator. You can use the AWS CLI and SDK to work with data in ScyllaDB. This is quite awesome.

DynamoDB has an export to S3 capability, but currently ScyllaDB does not. Not so awesome.

This repo is me looking into how one might scan an Alternator table in ScyllaDB and export to S3. This is very early stages and if Scylla Engineering adds this capability, this code will no longer be needed. But hopefully it can help serve in helping others to understand how to access Dynamo compatible data in Scylla.

The use case is exporting from ScyllaDB Alternator to an S3 bucket so data can be picked up by AWS Glue or GCP Cloud Data fusion (yes, with ScyllaDB you can run DynamoDB workloads on GCP) or other tools for data analysis. 

As this is early days, I'm aware it ain't too pretty.  At the moment, I'm just becoming familiar with the AWS Dynamo SDK.  The SDK is version 2. The preferred way to grab this is using maven.  

There's a lot of documentation on using the DynamoDB SDK, but it is dated.  Funny enough, if you see _"services.dynamodbv2"_ in the package being imported, this is the older SDK.

The concepts here are to
1. Scan the dynamo table
2. Convert the results to JSON
3. Package the JSON to a .zip file (saving as gzip)
4. Save to S3 (Saving to S3, but currently first to file system)

For 1, this code currently needs to be updated to run a parallel scan.

For 2, Dynamo returns results in what they call _"marshalled"_ JSON if using the CLI or exporting to S3.  In the ScanResponse, you'll see similar results, but in a text format, not JSON. 

**ScanResponse item.s() example:**
```
{Artist=AttributeValue(S=Taylor Swift), SongTitle=AttributeValue(S=Karma), AlbumTitle=AttributeValue(S=Midnights)}
```
 I'm using the EnhancedDocument to save the results as JSON, but this differs from what Dynamo exports to S3.

**Dynamo S3 export example:**
```
{"Item":{"Artist":{"S":"Taylor Swift"},"SongTitle":{"S":"Karma"},"AlbumTitle":{"S":"Midnights"}}}
```
**This code currently exports:**
```
{"Artist":"Taylor Swift","SongTitle":"Karma","AlbumTitle":"Midnights"}
```
The goal is to mimic Dynamo export, which stores each item on it's own line within a .zip file on S3, similar to:
```
{"Item":{"Artist":{"S":"Taylor Swift"},"SongTitle":{"S":"Karma"},"AlbumTitle":{"S":"Midnights"}}}
{"Item":{"Artist":{"S":"No One You Know"},"SongTitle":{"S":"Call Me Today"},"AlbumTitle":{"S":"Greatest Hits"}}}<br>
{"Item":{"Artist":{"S":"Scylla Seamonster"},"SongTitle":{"S":"Monstrously Fast"},"AlbumTitle":{"S":"Scale Them All"}}}
```
Now, the export to S3 for Dynamo also stores a manifest and additional info in the S3 bucket. I'm only focused on exporting the JSON at this time.

We have a MVP working here for saving a small table from Scylla Alternator to S3.  Moving on to parallelizing the scan as well as saving directly to S3. (File currenlty lands locally first.)


