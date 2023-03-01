package repository;

import domain.Friendship;
import domain.validator.FriendshipValidator;
import javafx.util.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipDBRepo extends AbstractDBRepo<Friendship, Pair<String,String>> {
    private final String table_name;

    public FriendshipDBRepo(FriendshipValidator val, String db_name, String t_name){
        super(val,db_name,t_name);
        this.table_name = t_name;
    }


    @Override
    public Friendship delete(Pair<String, String> id){
        String username1 = id.getKey();
        String username2 = id.getValue();
        String query ="DELETE FROM friendships WHERE username1='"+username1+"' AND username2='"+username2+"'";
        try {
            PreparedStatement st = connection.prepareStatement(query);
            st.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public Friendship findOne(Pair<String, String> id){
        String username1 = id.getKey();
        String username2 = id.getValue();
        Friendship entity=null;
        String querry ="SELECT * FROM friendships WHERE username1='"+username1+"' AND username2='"+username2+"'";
        try {
            PreparedStatement st = connection.prepareStatement(querry);
            ResultSet res = st.executeQuery();
            while(res.next()){
                entity = resultSetToEntity(res);
            }
        }
        catch (SQLException e){
            System.out.println(e);
        }
        return entity;
    }

    @Override
    public Friendship resultSetToEntity(ResultSet r){
        try{
            Friendship f = new Friendship(r.getString(1),r.getString(2),r.getString(4));
            return f;
        }
        catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String entityToDBString(Friendship f){
        String str;
        str = "('"+f.getFriendsFrom()+"','"+f.getUser1()+"','"+f.getUser2()+"','"+f.getStatus()+"')";
        return str;
    }
}
