package seed.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Froggy
 * 2017-03-02.
 */

@Document
public class Comment {
    @Indexed
    private ObjectId userId;

    private String content;

    public Comment(ObjectId userId, String content){
        this.userId = userId;
        this.content = content;
    }

    public void setUserId(ObjectId userId){
        this.userId=userId;
    }

    public ObjectId getUserId(ObjectId userId){
        return userId;
    }

    public void setContent(String content){
        this.content=content;
    }

    public String getContent(String content){
        return content;
    }

    protected Comment(){}
}
