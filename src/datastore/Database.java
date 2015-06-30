package datastore;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

/**
 * Main app
 */

public class Database
{

    private static volatile MongoClient mongoClient;
    private static volatile com.mongodb.DB db;

    public static MongoClient getMongoClient() throws UnknownHostException
    {
        if(mongoClient == null) {
            mongoClient = new MongoClient("localhost");
        }
        return mongoClient;
    }
}
