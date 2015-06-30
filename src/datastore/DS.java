package datastore;

import org.mongodb.morphia.Morphia;
import java.net.UnknownHostException;

public class DS {

    final public static Morphia morphia = new Morphia();
    private static volatile org.mongodb.morphia.Datastore datastore;

    public static org.mongodb.morphia.Datastore getDatastore() throws UnknownHostException {
        if (datastore == null) {
            datastore = morphia.createDatastore(Database.getMongoClient(), "wg");
            datastore.ensureIndexes();
        }
        return datastore;
    }
}
