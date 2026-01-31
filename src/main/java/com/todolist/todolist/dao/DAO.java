package com.todolist.todolist.dao;

import com.todolist.todolist.database.ConnectionDB;

import java.sql.Connection;
import java.util.List;

public abstract class DAO<T> {

    protected Connection connect = null;

    public DAO() {
        try {
            this.connect = ConnectionDB.getInstance();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public abstract List<T> findAll();

    public abstract T find(int id);

    public abstract boolean create(T t);

    public abstract boolean update(T t);

    public abstract boolean delete(T t);


}
