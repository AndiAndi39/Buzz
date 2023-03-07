package repository;

import domain.Message;
import domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageDBRepo {

    private String db_name;
   protected Connection connection;

    public MessageDBRepo(String _db_name){
        this.db_name = _db_name;
        try {
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+db_name,"postgres","postgre");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Message save(Message m){
        String query = "INSERT INTO messages(\"from\",\"to\",\"text\",\"time\") VALUES"+messageToDBString(m);
        try {
            PreparedStatement st = connection.prepareStatement(query);
            st.executeUpdate();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return m;
    }

    public Iterable<Message> findAll(){
        String query = "SELECT * FROM messages";
        List<Message> entities = new ArrayList<>();
        try {
            PreparedStatement st = connection.prepareStatement(query);
            ResultSet res = st.executeQuery();
            while (res.next()) {
                Message entity = resultSetToEntity(res);
                entities.add(entity);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return entities;
    }

    public String messageToDBString(Message m){
        String str;
        str = "('"+m.getFrom()+"','"+m.getTo()+"','"+m.getText()+"','"+m.getTime()+"')";
        return str;
    }

    public Message resultSetToEntity(ResultSet r){
        try{
            Message u = new Message(r.getString(1),r.getString(2),r.getString(3),r.getTimestamp(4).toLocalDateTime());
            return u;
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
