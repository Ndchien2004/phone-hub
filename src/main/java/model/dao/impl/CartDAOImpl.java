package model.dao.impl;

import model.dao.CartDAO;

public class CartDAOImpl implements CartDAO {

    public void createCart(int userId){
        String query = "INSERT INTO cart VALUES (?,?,?,?,?,?,?,?,?,?)";
    }
}
