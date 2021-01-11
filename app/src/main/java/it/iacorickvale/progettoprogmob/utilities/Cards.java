package it.iacorickvale.progettoprogmob.utilities;

import android.content.Context;

public class Cards {

    private String path;
    private String ref;
    private String type;


    public Cards(String path, String ref , String type) {
        this.path = path;
        this.ref = ref;
        this.type = type;
    }

    public String getPath() { return path; }

    public String getRef() { return ref; }

    public String getType() { return type; }

}
