package seed.web;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import seed.domain.User;
import seed.exception.UserNotFoundException;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

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

    @RequestMapping(method = RequestMethod.POST)
    User createUser(@RequestBody User user) {
        return userRepository.insert(user);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/password")
    void changeUserPassword(@RequestBody ObjectId id, @RequestBody String oldPassword, @RequestBody String newPassword) {
        User user = userRepository.findById(id);
        if(user.passwordAuthenticate(oldPassword)) {
            user.setPassword(newPassword);
        } else {
            throw new UserNotFoundException(id);
        }
    }

}
