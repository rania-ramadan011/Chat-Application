package rmiinterfaces;

import java.util.ArrayList;

public class Group {

    private String group_id;
    private String group_name;
    private ArrayList<Client> group;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public ArrayList<Client> getGroup() {
        return group;
    }

    public void setGroup(ArrayList<Client> group) {
        this.group = group;
    }

    
}
