package seed.domain;

import org.hibernate.validator.constraints.Email;

/**
 * Created by Macsnow on 2017/3/2.
 */
public class AuthCert {
    public String openid;
    @Email
    public String email;
    public String password;
    public boolean useWechat;
}
