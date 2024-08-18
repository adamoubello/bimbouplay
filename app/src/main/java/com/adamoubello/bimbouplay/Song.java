package com.adamoubello.bimbouplay;

public class Song {
    int id;
    String title;
    int numplays;
    int numlikes;

    public Song(String id, String title, String numplays, String numlikes) {

        try{
            this.id = Integer.parseInt(id);

        }catch (Exception e){
            this.id = 0;
        }
        this.title = title;
        this.numplays = Integer.parseInt(numplays);
        this.numlikes = Integer.parseInt(numlikes);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumplays() {
        return numplays;
    }

    public void setNumplays(int numplays) {
        this.numplays = numplays;
    }

    public int getNumlikes() {
        return numlikes;
    }

    public void setNumlikes(int numlikes) {
        this.numlikes = numlikes;
    }
}
