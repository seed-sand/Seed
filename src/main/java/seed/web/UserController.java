package seed.web;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seed.domain.AuthCert;
import seed.domain.User;
import seed.exception.IncorrectPasswordException;
import seed.exception.ResourceNotFoundException;
import seed.exception.UnauthorizedException;
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

    @Autowired
    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(method = POST)
    ResponseEntity<?> signup(@RequestBody User user, HttpSession httpSession) {
        return new ResponseEntity<>(userRepository.insert(user), HttpStatus.CREATED);
    }

    @RequestMapping(method = POST, value = "log-in")
    ResponseEntity<?> login(@RequestBody AuthCert authCert, HttpSession httpSession) {
        User user;
        if(authCert.useWechat) {
            user = this.userRepository.findByOpenId(authCert.openid)
                    .orElseThrow(() -> new ResourceNotFoundException(authCert.openid ,"user"));
        } else {
            user = this.userRepository.findByEmail(authCert.email)
                    .orElseThrow(() -> new ResourceNotFoundException(authCert.openid ,"user"));
        }

        return Optional.of(user).filter(user1 -> user1.passwordAuthenticate(authCert.password))
                .map(user1 -> {
                    httpSession.setAttribute("userId", user.getId());
                    return new ResponseEntity<>(user1, HttpStatus.OK);
                })
                .orElseThrow(IncorrectPasswordException::new);
    }

    @RequestMapping(method = PATCH, value = "/password")
    ResponseEntity<?> ChangePassword(@RequestBody String oldPassword,
                        @RequestBody String newPassword,
                        HttpSession httpSession) {

        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId ,"user"));
        return Optional.of(user).filter(user1 -> user1.passwordAuthenticate(oldPassword))
                .map(user1 -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseThrow(IncorrectPasswordException::new);
    }

    @RequestMapping(method = GET, value = "/profile")
    ResponseEntity<?> getProfile(HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseThrow(UnauthorizedException::new);
    }

    @RequestMapping(method = GET, value = "/log-out")
    ResponseEntity<?> logout(HttpSession httpSession) {
        httpSession.invalidate();
        return ResponseEntity.noContent().build();
    }


}
