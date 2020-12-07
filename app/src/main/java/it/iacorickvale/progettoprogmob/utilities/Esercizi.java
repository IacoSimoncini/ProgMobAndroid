package it.iacorickvale.progettoprogmob.utilities;

public class Esercizi {

    private String description;
    private String difficulty;
    private String name;

    public Esercizi(String description, String difficulty, String name){
        this.description = description;
        this.difficulty = difficulty;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getName() { return name; }
}
