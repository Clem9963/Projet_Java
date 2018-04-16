package com.projetisima.scores;

public class Person {
    private String pseudo;
    private int score;
    private int rank;

    public Person(String pseudo, int score, int rank){
        this.pseudo = pseudo;
        this.score = score;
        this.rank = rank;
    }

    public String getPseudo(){
        return this.pseudo;
    }

    public int getScore(){
        return this.score;
    }
    public String toString(){
        return this.pseudo + " : " + this.score;
    }
    public int getRank(){
        return this.rank;
    }
}
