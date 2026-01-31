package com.todolist.todolist.dao;

import java.util.Date;

public class Annonce {

    private Integer id;
    private String title;
    private String description;
    private String adress;
    private String mail;
    private Date date;

    public Annonce(Integer id, String title, String description, String adress, String mail, Date date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.adress = adress;
        this.mail = mail;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAdress() {
        return adress;
    }

    public String getMail() {
        return mail;
    }

    public Date getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
