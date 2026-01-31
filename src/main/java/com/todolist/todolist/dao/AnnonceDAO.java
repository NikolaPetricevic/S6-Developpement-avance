package com.todolist.todolist.dao;

import com.todolist.todolist.database.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnonceDAO extends DAO<Annonce> {

    @Override
    public boolean create(Annonce annonce) {
        String query = "INSERT INTO annonce (title, description, adress, mail) VALUES (?, ?, ?, ?)";

        try {
            Connection connect = ConnectionDB.getInstance();

            PreparedStatement state = connect.prepareStatement(query);

            state.setString(1, annonce.getTitle());
            state.setString(2, annonce.getDescription());
            state.setString(3, annonce.getAdress());
            state.setString(4, annonce.getMail());

            int result = state.executeUpdate();

            return result == 1;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Annonce> findAll() {
        List<Annonce> listeAnnonces = new ArrayList<>();
        String query = "SELECT * FROM annonce ORDER BY title";

        try {
            Connection connect = ConnectionDB.getInstance();
            Statement state = connect.createStatement();

            ResultSet rs = state.executeQuery(query);

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String adress = rs.getString("adress");
                String mail = rs.getString("mail");
                Timestamp date = rs.getTimestamp("date");

                Annonce annonce = new Annonce(id, title, description, adress, mail, date);
                listeAnnonces.add(annonce);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return listeAnnonces;
    }

    @Override
    public Annonce find(int id) {
        Annonce annonce = null;
        String query = "SELECT * FROM annonce WHERE id = ?";

        try {
            Connection connect = ConnectionDB.getInstance();
            PreparedStatement state = connect.prepareStatement(query);

            state.setInt(1, id);
            ResultSet rs = state.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                String adress = rs.getString("adress");
                String mail = rs.getString("mail");
                java.sql.Timestamp date = rs.getTimestamp("date");

                annonce = new Annonce(id, title, description, adress, mail, date);
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return annonce;
    }

    @Override
    public boolean update(Annonce annonce) {
        String query = "UPDATE annonce SET title = ?, description = ?, adress = ?, mail = ? WHERE id = ?";

        try {
            Connection connect = ConnectionDB.getInstance();
            PreparedStatement state = connect.prepareStatement(query);

            state.setString(1, annonce.getTitle());
            state.setString(2, annonce.getDescription());
            state.setString(3, annonce.getAdress());
            state.setString(4, annonce.getMail());

            state.setInt(5, annonce.getId());

            int result = state.executeUpdate();

            return result == 1;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Annonce annonce) {
        String query = "DELETE FROM annonce WHERE id = ?";

        try {
            Connection connect = ConnectionDB.getInstance();
            PreparedStatement state = connect.prepareStatement(query);

            state.setInt(1, annonce.getId());
            int result = state.executeUpdate();

            return result == 1;
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
