package iliker.utils.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
 class MyAuthenticator extends Authenticator{
    private String userName=null;
    private String password=null;
       
     MyAuthenticator(String username, String password) {
        this.userName = username;   
        this.password = password;   
    }   
    protected PasswordAuthentication getPasswordAuthentication(){  
        return new PasswordAuthentication(userName, password);  
    }  
}  
