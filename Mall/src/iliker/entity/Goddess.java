package iliker.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LIXINRONG on 2016/8/26.
 */
public class Goddess implements Serializable{
    private List<UserInfo> users;
    private List<Praise> praises;

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public List<Praise> getPraises() {
        return praises;
    }

    public void setPraises(List<Praise> praises) {
        this.praises = praises;
    }

}
