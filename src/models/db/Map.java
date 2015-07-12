package models.db;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value = "map", noClassnameStored = true)
@Indexes(@Index("x, y"))
public class Map {
    @Id
    public ObjectId _id;
    public Integer x;
    public Integer y;
    public Byte landType;
    public ObjectId build_id;
}
