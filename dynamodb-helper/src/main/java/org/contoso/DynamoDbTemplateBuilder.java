package org.contoso;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.awspring.cloud.dynamodb.DynamoDbTableSchemaResolver;
import io.awspring.cloud.dynamodb.DefaultDynamoDbTableSchemaResolver;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;

import java.util.Collections;

public class DynamoDbTemplateBuilder {

    private String tableName;

    private DynamoDbTemplateBuilder() {
        // Private constructor to enforce usage of the builder() method
    }

    public static DynamoDbTemplateBuilder builder() {
        return new DynamoDbTemplateBuilder();
    }

    public DynamoDbTemplateBuilder withTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DynamoDbTemplate build() {
        if (tableName == null) {
            throw new IllegalStateException("Table name must be provided.");
        }

        // Create a default DynamoDbClient and DynamoDbEnhancedClient
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_1) // Default region
            .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

        // Create the table name resolver
        SimpleDynamoDbTableNameResolver tableNameResolver = new SimpleDynamoDbTableNameResolver(tableName);

        // Use the default schema resolver
        DynamoDbTableSchemaResolver schemaResolver = new DefaultDynamoDbTableSchemaResolver(Collections.emptyList());

        // Create and return the DynamoDbTemplate
        return new DynamoDbTemplate(enhancedClient, schemaResolver, tableNameResolver);
    }
}