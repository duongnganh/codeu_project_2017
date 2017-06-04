/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package codeu.chat;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * A minimal application that connects to Cloud Bigtable using the native HBase API
 * and performs some basic operations.
 */
public class createTableForUI {

  // Refer to table metadata names by byte array in the HBase API
  private static final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("cf1");

  /**
   * Connects to Cloud Bigtable, runs some basic operations and prints the results.
   */
  private static void create(String projectId, String instanceId, String[] tableNames) {

    // [START connecting_to_bigtable]
    // Create the Bigtable connection, use try-with-resources to make sure it gets closed
    try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {

      // The admin API lets us create, manage and delete tables
      Admin admin = connection.getAdmin();
      // [END connecting_to_bigtable]

      // [START creating_a_table]
      // Create a table with a single column family
      for (String tableName : tableNames){
        HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(tableName));
        descriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY_NAME));

        print("Create table " + descriptor.getNameAsString());
        admin.createTable(descriptor);
      }
      // [END creating_a_table]

    } catch (IOException e) {
      System.err.println("Exception while running HelloWorld: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }

    System.exit(0);
  }

  private static void print(String msg) {
    System.out.println(msg);
  }

  public static void main(String[] args) {
    String projectId = requiredProperty("projectId");
    String instanceId = requiredProperty("instanceId");

    // TODO: hard code
    String[] tableNames = 
    {"Paris-Food", "Paris-Culture", "Paris-Attractions", 
    "Berlin-Food", "Berlin-Culture", "Berlin-Attractions", 
    "London-Food", "London-Culture", "London-Attractions"};

    create(projectId, instanceId, tableNames);
  }

  private static String requiredProperty(String prop) {
    String value = System.getProperty(prop);
    if (value == null) {
      throw new IllegalArgumentException("Missing required system property: " + prop);
    }
    return value;
  }

}
