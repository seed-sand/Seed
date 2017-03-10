package seed.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Macsnow on 2017/3/8.
 */
@Document
public class Event {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Indexed
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId objectiveId;

    @JsonSerialize(using = ToStringSerializer.class)
    private String event;

    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime endTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private boolean status = true;

    public Event(String event, DateTime endTime, boolean status) {
        this.event = event;
        this.endTime = endTime;
        this.status = status;
    }

    public Event(String event, DateTime endTime) {
        this.event = event;
        this.endTime = endTime;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

    protected Event(){}
}
