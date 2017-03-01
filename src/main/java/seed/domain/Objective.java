package seed.domain;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


/**
 * Created by Macsnow on 2017/3/1.
 */
@Document
public class Objective {
    @Id
    private ObjectId id;

    @Indexed
    private ObjectId userId;

    @Indexed
    private ObjectId groupId;

    private String title;
    private String description;
    private DateTime deadline;
    private int priority;
    private String scope;
    private String status;
    private List<ObjectId> assignment;
    private List<ObjectId> comments;

    public Objective(String title, String scope) {
        this.title = title;
        this.scope = scope;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        id = id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public ObjectId getGroupId() {
        return groupId;
    }

    public void setGroupId(ObjectId groupId) {
        this.groupId = groupId;
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

    public DateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(DateTime deadline) {
        this.deadline = deadline;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ObjectId> getAssignment() {
        return assignment;
    }

    public void setAssignment(List<ObjectId> assignment) {
        this.assignment = assignment;
    }

    public List<ObjectId> getComments() {
        return comments;
    }

    public void setComments(List<ObjectId> comments) {
        this.comments = comments;
    }

    protected Objective() {

    }


}
