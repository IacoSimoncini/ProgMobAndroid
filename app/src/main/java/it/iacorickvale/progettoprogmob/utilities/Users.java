package it.iacorickvale.progettoprogmob.utilities;

import android.net.Uri;

public class Users {
    private String firstname;
    private String lastname;
    private String id;
    private String uri;
    private String tot_cal;
    private String goal;

    public Users(String firstname, String lastname, String id, String uri, String tot_cal, String goal){
        this.firstname = firstname;
        this.lastname = lastname;
        this.id = id;
        this.uri = uri;
        this.tot_cal = tot_cal;
        this.goal = goal;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getId() {
        return id;
    }

    public String getUri() {return uri;}

    public String getGoal() {return goal;}

    public void setGoal(String us_goal) {goal = us_goal;}

    public String getTot_cal() {return tot_cal;}

    public void setTot_cal(String cal_goal) {tot_cal = cal_goal;}
}
