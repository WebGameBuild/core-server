package models.db;

import datastore.DS;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.Date;

@Entity(value = "user", noClassnameStored = true)
public class User {
    @Id
    public ObjectId id;
    @Indexed(unique=true)
    public String username;
    public String password_hash;
    public Date created_at;
    public Date updated_at;
    public Date visited_at;

    public void touch()
    {
        Query<User> updateQuery = DS.getDatastore().createQuery(User.class).field("_id").equal(this.id);
        UpdateOperations<User> updateOperations = DS.getDatastore()
                .createUpdateOperations(User.class)
                .set("visited_at", new Date());

        DS.getDatastore().updateFirst(updateQuery, updateOperations);
    }

}
