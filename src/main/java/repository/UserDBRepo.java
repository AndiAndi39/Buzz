package repository;
import domain.User;
import domain.validator.UserValidator;
import domain.validator.Validator;

import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDBRepo extends AbstractDBRepo<User, String> {

    private final String table_name;

    public UserDBRepo(UserValidator val, String db_name,String t_name){
        super(val,db_name,t_name);
        this.table_name = t_name;
    }
    @Override
    public User delete(String username){
        String querry = "DELETE FROM users WHERE username='"+username+"'";
        User user = findOne(username);
        try {
            PreparedStatement st = connection.prepareStatement(querry);
            st.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return user;
    }

    @Override
    public User findOne(String username){
        String querry = "SELECT * FROM " + this.table_name +" WHERE username=\'"+username+"\'";
        try {
            PreparedStatement st = connection.prepareStatement(querry);
            ResultSet res = st.executeQuery();
            User entity=null;
            while(res.next()){
                entity = resultSetToEntity(res);
            }
            return entity;
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public User resultSetToEntity(ResultSet r){
        try{
            User u = new User(r.getString(2),r.getString(1),r.getString(4),r.getString(3),r.getString(5));
            return u;
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public String entityToDBString(User u){
        String str;
        str = "('"+u.getUsername()+"','"+u.getName()+"','"+u.getEmail()+"','"+u.getPassword()+"','"+u.getSalt()+"')";
        return str;
    }

}
