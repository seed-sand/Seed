package seed.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Froggy
 * 2017-03-02.
 */

@Document
public class Comment {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Indexed
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId objectiveId;

    @Indexed
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId userId;

    @NotEmpty
    @JsonSerialize(using = ToStringSerializer.class)
    private String content;

    public Comment(ObjectId userId, String content){
        this.userId = userId;
        this.content = content;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(ObjectId objectiveId) {
        this.objectiveId = objectiveId;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    protected Comment(){}
}
