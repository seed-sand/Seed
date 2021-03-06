package seed.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static seed.util.Encryption.encrypt;

/**
 * Created by Macsnow on 2017/3/1.
 */

@Document
public class User {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

//    @NotEmpty(message = "password!")
    private String password;

//    @NotEmpty(message = "username!")
    private String username;

    @Indexed(unique = true)
    @Email
    private String email;

    @Indexed(unique = true)
    private String openId;

    private String avatar;
    private List<ObjectId> ObjectiveCreated = new ArrayList<>(0);
    private List<ObjectId> ObjectiveJoined = new ArrayList<>(0);
    private List<ObjectId> ObjectiveListCreated = new ArrayList<>(0);

    public User(String username, String identity, String password, boolean useWechat) {
        this.username = username;
        this.setPassword(password);
        if(useWechat) {
            this.openId = identity;
        } else {
            this.email = identity;
        }
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = encrypt("SHA1", password);
    }

    public boolean passwordAuthenticate(String password) {
        return this.password.equals(encrypt("SHA1", password));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public List<ObjectId> getObjectiveCreated() {
        return ObjectiveCreated;
    }

    public void setObjectiveCreated(List<ObjectId> objectiveCreated) {
        ObjectiveCreated = objectiveCreated;
    }

    public List<ObjectId> getObjectiveJoined() {
        return ObjectiveJoined;
    }

    public void setObjectiveJoined(List<ObjectId> objectiveJoined) {
        ObjectiveJoined = objectiveJoined;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<ObjectId> getObjectiveListCreated() {
        return ObjectiveListCreated;
    }

    public void setObjectiveListCreated(List<ObjectId> objectiveListCreated) {
        ObjectiveListCreated = objectiveListCreated;
    }

    protected User() {

    }

}
