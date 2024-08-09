package com.scylla;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;
import software.amazon.awssdk.services.guardduty.model.ScanResult;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class Scan {
    public static void main(String[] args) {

        //String tableName = args[0];
        //String tableName = "PeteExportTest";
        String tableName = "MusicCollection";
        System.out.println("Scanning your Amazon DynamoDB table: "+tableName+" \n");
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        //Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder()
                //.region(region)
                //.endpointOverride(URI.create("http://34.23.43.173:8000"))
                .endpointOverride(URI.create("http://node-0.gce-us-east-1.76474865b7745a2ec5bd.clusters.scylla.cloud:8000"))
                .build();

              try {
                    ScanRequest request = ScanRequest
                    .builder()
                    .tableName(tableName)
                    //.limit(2)
                    //.filterExpression("Artist = :Artist")
                    //.expressionAttributeValues(
                      /// Map.of(":Artist", AttributeValue.builder().s("Taylor Swift").build())) // Using Java 9+ Map.of
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


                        list.add(t.toJson());

                        String filePath = "/Users/wpaven/ddb_to_scylla_migration/import_export_java/"; 
                        /*try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath)))  
                        { 
                            for (String str : list) { 
                               writer.write(str); 
                               writer.newLine();  
                            } 
                         System.out.println("ArrayList written to file successfully."); 
                        } catch (IOException e) { 
                            e.printStackTrace(); 
                        } 
                       */
                        try{
                          sb.append(t.toJson());
                          sb.append(System.getProperty("line.separator"));

                          File f = new File(filePath+"output.zip");
                          ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
                          ZipEntry e = new ZipEntry("output.txt");
                          out.putNextEntry(e);

                          byte[] data = sb.toString().getBytes();
                          out.write(data, 0, data.length);
                          out.closeEntry();
                          out.close();
                        }catch(IOException e){
                           System.out.println("ERROR: "+e.getStackTrace());
                        }
                    }

        } catch (DynamoDbException e) {
            e.getStackTrace();
        }
       
        //listAllTables(ddb);
        ddb.close();
    }

    
}
