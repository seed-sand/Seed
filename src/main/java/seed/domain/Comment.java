package seed.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by Froggy
 * 2017-03-02.
 */

public class Comment {
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

    public void setUserId(ObjectId userId){
        this.userId = userId;
    }

    public ObjectId getUserId(ObjectId userId){
        return userId;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(String content){
        return content;
    }

    protected Comment(){}
}
