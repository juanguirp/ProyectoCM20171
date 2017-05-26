package co.edu.udea.compumovil.gr04_20171.proyectocm20171.cyclist;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonnatan on 27/04/17.
 */
@IgnoreExtraProperties
public class Cyclist {
    private String username;
    private String phonenumber;
    private String city;
    private String age;
    private Map<String, Boolean> user = new HashMap<String, Boolean>();
    private Map<String, Boolean> groups = new HashMap<String, Boolean>();

    public Cyclist() {
    }

    public Cyclist(String username, String phonenumber, String city, String age) {
        this.username = username;
        this.phonenumber = phonenumber;
        this.city = city;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String address) {
        this.city = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


    public Map getUser() {
        return user;
    }

    public void setUser(Object userObject) {
        if(userObject instanceof Map){
            this.user.putAll((Map<String, Boolean>) userObject);
        }
    }

    public Map getGroups() {
        return groups;
    }

    public void setGroups(Object grouṕObject) {
        if(grouṕObject instanceof Map){
            this.groups.putAll((Map<String, Boolean>) grouṕObject);
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("phonenumber", phonenumber);
        result.put("city", city);
        result.put("age", age);
        result.put("groups", groups);
        result.put("user", user);

        return result;
    }
}
