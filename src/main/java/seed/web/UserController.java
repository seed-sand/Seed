package seed.web;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seed.domain.AuthCert;
import seed.domain.User;
import seed.exception.IncorrectPasswordException;
import seed.exception.UserNotFoundException;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;


import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Macsnow on 2017/3/1.
 */

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final ObjectiveRepository objectiveRepository;

    @Autowired
    UserController(UserRepository userRepository, ObjectiveRepository objectiveRepository) {
        this.userRepository = userRepository;
        this.objectiveRepository = objectiveRepository;
    }

    @RequestMapping(method = POST)
    User signup(@RequestBody User user, HttpSession httpSession) {
        return userRepository.insert(user);
    }

    @RequestMapping(method = POST, value = "log-in")
    User login(@RequestBody AuthCert authCert, HttpSession httpSession) {
        validateUser(authCert.useWechat ? authCert.openid : authCert.email, authCert.useWechat);
        User user = this.userRepository.findByEmail(authCert.useWechat ? authCert.openid : authCert.email)
                                       .filter(user1 -> user1.passwordAuthenticate(authCert.password))
                                       .orElseThrow(IncorrectPasswordException::new);
        httpSession.setAttribute("userId", user.getId());
        return user;
    }

    @RequestMapping(method = PATCH, value = "/password")
    User ChangePassword(@RequestBody String oldPassword,
                        @RequestBody String newPassword,
                        HttpSession httpSession) {

        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        validateUser(userId);
        User user = userRepository.findById(userId)
                                  .filter(user1 -> validateUser(userId))
                                  .orElseThrow(UserNotFoundException::new);
        user = Optional.of(user).filter(user1 -> user1.passwordAuthenticate(oldPassword))
                                .orElseThrow(IncorrectPasswordException::new);
        return userRepository.save(user);
    }

    @RequestMapping(method = GET, value = "/profile")
    User getProfile(HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                             .filter(user -> validateUser(userId))
                             .orElseThrow(UserNotFoundException::new);
    }

    @RequestMapping(method = GET, value = "/log-out")
    ResponseEntity<?> logout(HttpSession httpSession) {
        httpSession.invalidate();
        return ResponseEntity.noContent().build();
    }

    private boolean validateUser(ObjectId id) {
        return this.userRepository.findById(id).isPresent();
    }

    private boolean validateUser(String identity, boolean useWechat) {
        if(useWechat) {
            return this.userRepository.findByOpenId(identity).isPresent();
        } else {
            return this.userRepository.findByEmail(identity).isPresent();
        }
    }

}
