package org.contoso;

import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;

public class SimpleDynamoDbTableNameResolver implements DynamoDbTableNameResolver {
    private final String tableName;

    public SimpleDynamoDbTableNameResolver(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public <T> String resolve(Class<T> clazz) {
        return tableName;
    }
}
