package com.scylla;

//import com.scylla.ConfigFileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import java.io.File;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Scan {

    static void saveToS3(String bucketName, String keyName, String profile, String fileToSave) {
        System.out.println("We will be saving to S3!");
        String filePath = fileToSave; //"/path/to/your/file.txt";

        System.out.println("====File to Save"+filePath);

        Region region = Region.US_EAST_1; // Replace with your desired region
        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create(profile))
                .build();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.putObject(request, RequestBody.fromFile(new File(filePath)));
            System.out.println("File uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        ConfigFileReader cr = new ConfigFileReader();

        String tableName = cr.getTableName();
        System.out.println("Scanning your Amazon DynamoDB table: "+tableName+" \n");

        String filePath = cr.getFilePath();
        String gzipFileToSave = cr.getGzipFileToSave();
        String scyllaEndpointURL = cr.getendPointOverrideUrl();
        String bucketName = cr.getBucketName();
        String keyName = cr.getKeyName();
        String profile = cr.getProfile();

        //Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder()
                //.region(region) NOT NEEDED FOR SCYLLA
                .endpointOverride(URI.create(scyllaEndpointURL))
                .build();

              try {
                    ScanRequest request = ScanRequest
                    .builder()
                    .tableName(tableName)
                    //.limit(2)
                    //.filterExpression("Artist = :Artist")
                    //.expressionAttributeValues(
                    // Map.of(":Artist", AttributeValue.builder().s("Taylor Swift").build())) // Using Java 9+ Map.of
                    .build();

                    //ScanResponse result = ddb.scan(request);
                    ScanIterable result = ddb.scanPaginator(request);

                    for (Map<String, AttributeValue> item : result.items()) {
                        Set<String> keys = item.keySet();
                        for (String key : keys) {
                            System.out.println("The key name is " + key + "\n");
                            System.out.println("The value is " + item.get(key).s());
                        }
                        System.out.println("the item is: "+item);
                        EnhancedDocument t = EnhancedDocument.fromAttributeValueMap(item);
                        System.out.println(t.toJson());

                        sb.append(t.toJson());
                        sb.append(System.getProperty("line.separator"));

                    }

        } catch (DynamoDbException e) {
            e.getStackTrace();
        }

        try ( 
             FileOutputStream outputStream     = new FileOutputStream(filePath+gzipFileToSave);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)
            ) 
            {
                byte[] data = sb.toString().getBytes();
                gzipOutputStream.write(data);
            
           /*  originally saved as .zip. jury out on how we finally export...
            File f = new File(filePath+"output.zip");
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
            ZipEntry e = new ZipEntry(fileToSave);
            out.putNextEntry(e);

            byte[] data = sb.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
            out.close();
           */
            
          }catch(IOException e){
             System.out.println("ERROR: "+e.getStackTrace());
          }
          saveToS3(bucketName, keyName, profile, filePath+gzipFileToSave);
          ddb.close();

    }

    
}
