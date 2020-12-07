package it.iacorickvale.progettoprogmob.utilities;

import android.net.Uri;

public class Users {
    private String firstname;
    private String lastname;
    private String id;
    private String uri;

    public Users(String firstname, String lastname, String id, String uri){
        this.firstname = firstname;
        this.lastname = lastname;
        this.id = id;
        this.uri = uri;
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
}
