package seed.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by Macsnow on 2017/3/1.
 */

@Document
public class ObjectiveList {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId userId;

    private String title;
    private String description;

    private List<ObjectId> objectives;

    public ObjectiveList(String title) {
        this.title = title;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ObjectId> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<ObjectId> objectives) {
        this.objectives = objectives;
    }

    protected ObjectiveList() {

    }
}