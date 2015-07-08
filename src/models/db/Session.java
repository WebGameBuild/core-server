package models.db;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;

import java.util.Date;

@Entity(value = "session", noClassnameStored = true)
public class Session {
    @Id
    public ObjectId _id;
    public ObjectId user_id;
    public String token;
    public Date created_at;

    @PrePersist
    public void prePersist() {
        if (created_at == null) {
            created_at = new Date();
        }
    }

}
