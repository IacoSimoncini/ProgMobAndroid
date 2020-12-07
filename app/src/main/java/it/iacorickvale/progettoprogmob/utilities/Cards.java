package it.iacorickvale.progettoprogmob.utilities;

import android.content.Context;

public class Cards {

    private String path;
    private String ref;

    public Cards(String path, String ref) {
        this.path = path;
        this.ref = ref;
    }

    public String getPath() { return path; }

    public String getRef() { return ref; }

}
