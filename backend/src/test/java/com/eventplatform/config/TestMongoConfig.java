package com.eventplatform.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.testcontainers.containers.MongoDBContainer;

@Configuration
@EnableMongoRepositories(basePackages = "com.eventplatform.repository")
public class TestMongoConfig extends AbstractMongoClientConfiguration {

    private static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer("mongo:7.0");

    static {
        MONGO_CONTAINER.start();
    }

    @Override
    protected String getDatabaseName() {
        return "event-platform-test";
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(MONGO_CONTAINER.getReplicaSetUrl());
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public boolean autoIndexCreation() {
        return true;
    }

}