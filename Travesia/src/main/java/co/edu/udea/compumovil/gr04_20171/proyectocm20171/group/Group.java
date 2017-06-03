package co.edu.udea.compumovil.gr04_20171.proyectocm20171.group;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonnatan on 30/05/17.
 */

public class Group {

    private String groupName;
    private Map<String, Boolean> member = new HashMap<String, Boolean>();

    public Group() {
    }

    public Group(String groupName, Map<String, Boolean> member) {
        this.groupName = groupName;
        this.member = member;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Map<String, Boolean> getMember() {
        return member;
    }

    public void setMember(Map<String, Boolean> member) {
        if(member instanceof Map){
            this.member.putAll((Map<String, Boolean>) member);
        }
    }
}
