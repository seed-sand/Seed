package seed.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seed.util.Encryption;

import java.util.ArrayList;
import java.util.Collections;


/**
 * @Project: seed
 * @Package seed.web
 * @auther:Vanderchen
 * @date 2017/3/13 10:11
 */
@RestController
@RequestMapping("/connecter")
public class AuthenticController {

    private static String accessToken = "seedAccesstoken";

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<?> check(@RequestParam(value = "signature") String signature,
                            @RequestParam(value = "timestamp") String timestamp,
                            @RequestParam(value = "nonce") String nonce,
                            @RequestParam(value = "echostr") String echostr){
        if (checkSignature(signature,timestamp,nonce))
            return new ResponseEntity<Object>(echostr, HttpStatus.OK);
    }
    private static boolean checkSignature(String signature, String timestamp, String nonce){
        ArrayList<String> arr = new ArrayList<String>();
        arr.add(accessToken);
        arr.add(timestamp);
        arr.add(nonce);
        Collections.sort(arr);

        StringBuilder check = new StringBuilder(Encryption.encrypt("SHA-1",arr.toString()));

        return check != null && check.toString().equals(signature.toUpperCase());
    }
}
