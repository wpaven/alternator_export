package com.scylla;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public class  ConfigFileReader {

  private Properties properties;
  private final String configFilePath = "configs/app.config";
  InputStream inputStream;

  public ConfigFileReader() {

     File  ConfigFile=new File(configFilePath);
     try {
           FileInputStream configFileReader=new FileInputStream(ConfigFile);
           properties = new Properties();

           try {
                 properties.load(configFileReader);
                 configFileReader.close();
               } catch (IOException e) 
               {
                  System.out.println(e.getMessage());
                  System.out.println(e.getStackTrace());
               }

         }  catch (FileNotFoundException e) 
         {
             System.out.println(e.getMessage());
             throw new RuntimeException("app.config not found at config file path " + configFilePath);
        }

}  


public String getendPointOverrideUrl() {
   String endPointUrl= properties.getProperty("endPointOverride");

    if(endPointUrl != null)
       return endPointUrl;   
    else
       throw new RuntimeException("endPointURL not specified in the app.config file.");

}

public String getProfile() {
    String profile= properties.getProperty("profile");
 
     if(profile != null)
        return profile;   
     else
        throw new RuntimeException("profile not specified in the app.config file.");
 
 }

 public String getBucketName() {
    String bucketName= properties.getProperty("bucketName");
 
     if(bucketName != null)
        return bucketName;   
     else
        throw new RuntimeException("bucketName not specified in the app.config file.");
 
 }

 public String getKeyName() {
    String keyName= properties.getProperty("keyName");
 
     if(keyName != null)
        return keyName;   
     else
        throw new RuntimeException("keyName not specified in the app.config file.");
 
 }

 public String getTableName() {
    String tableName= properties.getProperty("tableName");
 
     if(tableName != null)
        return tableName;   
     else
        throw new RuntimeException("tableName not specified in the app.config file.");
 
 }

 public String getFilePath() {
    String filePath= properties.getProperty("filePath");
 
     if(filePath != null)
        return filePath;   
     else
        throw new RuntimeException("filePath not specified in the app.config file.");
 
 }

 public String getGzipFileToSave() {
    String gzipFileName= properties.getProperty("gzipFileToSave");
 
     if(gzipFileName != null)
        return gzipFileName;   
     else
        throw new RuntimeException("gzipFileToSave not specified in the app.config file.");
 
 }

}

