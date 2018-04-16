package com.projetisima.scores;

public class ScoreLocal {

    private int id;
    private Long date;
    private int score;

    // Constructeur
    public ScoreLocal(int id, Long date, int score) {
        this.id = id;
        this.date = date;
        this.score = score;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Long getDate(){
        return this.date;
    }

    public void setDate(Long date){
        this.date = date;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        this.score = score;
    }

}
