package domain;

import javafx.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Friendship extends Entity<Pair<String,String>> {

    private String user1;
    private String user2;

    private String status;
    LocalDate friendsFrom;

    public String toString() {
        return user1 + " si " + user2+ " sunt prieteni din: " + friendsFrom.format(DateTimeFormatter.ISO_DATE);
    }
    public Friendship(String user1, String user2, String status){
        this.user1 = user1;
        this.user2 = user2;
        this.friendsFrom = LocalDate.now();
        this.status = status;
    }
    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public LocalDate getFriendsFrom(){
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDate date){
        this.friendsFrom = date;
    }

    public String getStatus() {return status;}
    public void setStatus(){this.status = status;}
}
