package it.iacorickvale.progettoprogmob.utilities;

public class Esercizi {

    private String description;
    private String difficulty;
    private String name;
    private String cal;
    private static String uri;

    public Esercizi(String description, String difficulty, String name, String cal, String uri){
        this.description = description;
        this.difficulty = difficulty;
        this.name = name;
        this.cal = cal;
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getName() { return name; }

    public String getCal() { return cal; }

    public String getUri() {return uri;}

    public static void setUri(String videoUri) { uri = videoUri;}
}
