package com.projetisima.scores;

public class ScoreMondial {

    private int id;
    private String date;
    private String score;
    private String pseudo;

    // Constructeur
    public ScoreMondial(int id, String date, String score, String pseudo) {
        this.id = id;
        this.date = date;
        this.score = score;
        this.pseudo = pseudo;
    }

    public int getId(){ return this.id;}

    public void setId(int id){
        this.id = id;
    }

    public String getDate(){
        return this.date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getScore(){
        return this.score;
    }

    public void setScore(String score){
        this.score = score;
    }

    public void sePseudo(String pseudo){this.pseudo = pseudo;}

    public String getPseudo(){return this.pseudo;}

}
