package domain;

import domain.Entity;
import utils.myFunction;

import java.security.MessageDigest;

public class User extends Entity<String> {
    private String name;
    private String username;
    private String password;
    private String email;
    private String salt;

    public String toString() {
        return "Utilizator: " + username +" Nume: " + name + " Email: " + email;
    }
    public User(String name, String username, String password, String email, String salt) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {return email;}
    public void setEmail(String email){this.email = email;}

    public String getSalt() {return salt;}
    public void setSalt(String salt){this.salt = salt;}
}
