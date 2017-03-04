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
import seed.exception.ResourceNotFoundException;
import seed.exception.UnauthorizedException;
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
        User user;
        if(authCert.useWechat) {
            user = this.userRepository.findByOpenId(authCert.openid)
                    .orElseThrow(() -> new ResourceNotFoundException(authCert.openid ,"user"));
        } else {
            user = this.userRepository.findByEmail(authCert.email)
                    .orElseThrow(() -> new ResourceNotFoundException(authCert.openid ,"user"));
        }

        System.out.println(user.passwordAuthenticate(authCert.password));
        user = Optional.of(user).filter(user1 -> user1.passwordAuthenticate(authCert.password))
                                .orElseThrow(IncorrectPasswordException::new);
        httpSession.setAttribute("userId", user.getId());
        return user;
    }

    @RequestMapping(method = PATCH, value = "/password")
    User ChangePassword(@RequestBody String oldPassword,
                        @RequestBody String newPassword,
                        HttpSession httpSession) {

        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId ,"user"));
        user = Optional.of(user).filter(user1 -> user1.passwordAuthenticate(oldPassword))
                                .orElseThrow(IncorrectPasswordException::new);
        return userRepository.save(user);
    }

    @RequestMapping(method = GET, value = "/profile")
    User getProfile(HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                             .orElseThrow(UnauthorizedException::new);
    }

    @RequestMapping(method = GET, value = "/log-out")
    ResponseEntity<?> logout(HttpSession httpSession) {
        httpSession.invalidate();
        return ResponseEntity.noContent().build();
    }


}
