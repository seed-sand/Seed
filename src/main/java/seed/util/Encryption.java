package seed.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Macsnow on 2017/3/6.
 */
public class Encryption {
    public static String encrypt(String algorithm, String clearText) {
        try {
            MessageDigest pwd = MessageDigest.getInstance(algorithm);
            pwd.update(clearText.getBytes());
            return HexBin.encode(pwd.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No Such Algorithm: " + algorithm);
        }
    }
}
