package models.db;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

@Entity(value = "user", noClassnameStored = true)
public class User {
    @Id
    public ObjectId id;
    public String username;
    public String password_hash;
    public Date created_at;
    public Date updated_at;
    public Date visited_at;


}
