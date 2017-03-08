package seed.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


/**
 * Created by Macsnow on 2017/3/1.
 */
@Document
public class Objective {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Indexed
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId userId;

    @Indexed
    private ObjectId listId;

    @NotEmpty
    private String title;
    private String description;

    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime startTime;
    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime deadline;
    private int priority;
    private boolean status = true;
    private List<ObjectId> assignment;

    @Field("events")
    private List<Event> events;

    @Field("comments")
    private List<Comment> comments;

    public Objective(String title) {
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

    public ObjectId getListId() {
        return listId;
    }

    public void setListId(ObjectId listId) {
        this.listId = listId;
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<ObjectId> getAssignment() {
        return assignment;
    }

    public void setAssignment(List<ObjectId> assignment) {
        this.assignment = assignment;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    protected Objective() {

    }


}
