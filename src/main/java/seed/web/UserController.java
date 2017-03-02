package seed.web;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seed.domain.AuthCert;
import seed.domain.User;
import seed.exception.IncorrectPasswordException;
import seed.exception.UserNotFoundException;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;
import java.util.stream.Collectors;


import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Macsnow on 2017/3/1.
 */

@RestController
@RequestMapping("/user")
public class UserController {
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final ObjectiveRepository objectiveRepository;

    @Autowired
    UserController(MongoTemplate mongoTemplate, UserRepository userRepository, ObjectiveRepository objectiveRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.objectiveRepository = objectiveRepository;
    }

    @RequestMapping(method = POST)
    public User signup(@RequestBody User user, HttpSession httpSession) {
        return userRepository.insert(user);
    }

    @RequestMapping(method = POST, value = "log-in")
    public User login(@RequestBody AuthCert authCert, HttpSession httpSession) {
        validateUser(authCert.useWechat ? authCert.openid : authCert.email, authCert.useWechat);
        User user = this.userRepository.findByEmail(authCert.useWechat ? authCert.openid : authCert.email)
                                       .filter(user1 -> user1.passwordAuthenticate(authCert.password))
                                       .orElseThrow(IncorrectPasswordException::new);
        httpSession.setAttribute("userId", user.getId());
        return user;
    }

    @RequestMapping(method = PATCH, value = "/password")
    void ChangePassword(@RequestBody String oldPassword,
                        @RequestBody String newPassword,
                        HttpSession httpSession) {

        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        validateUser(userId);
        User user = userRepository.findById(userId)
                                  .map(user1 -> user1)
                                  .orElseThrow(IncorrectPasswordException::new);
        if(user.passwordAuthenticate(oldPassword)) {
            user.setPassword(newPassword);
            userRepository.save(user);
        } else {
            throw new IncorrectPasswordException();
        }
    }

    @RequestMapping(method = GET, value = "/profile")
    User getProfile(HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        validateUser(userId);
        return userRepository.findById(userId).get();
    }

    private void validateUser(ObjectId id) {
        this.userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id));
    }

    private void validateUser(String identity, boolean useWechat) {
        if(useWechat) {
            this.userRepository.findByOpenId(identity).orElseThrow(
                    () -> new UserNotFoundException(identity));
        } else {
            this.userRepository.findByEmail(identity).orElseThrow(
                    () -> new UserNotFoundException(identity));
        }
    }

}
