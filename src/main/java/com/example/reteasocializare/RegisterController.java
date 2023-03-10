package com.example.reteasocializare;

import domain.User;
import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import repository.FriendshipDBRepo;
import repository.MessageDBRepo;
import repository.UserDBRepo;
import service.Service;
import utils.LastUserLogged;
import utils.myFunction;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterController implements Initializable {
    UserValidator uval = new UserValidator();
    UserDBRepo urepo  = new UserDBRepo(uval, "buzz","users");
    FriendshipValidator fval = new FriendshipValidator();
    FriendshipDBRepo frepo = new FriendshipDBRepo(fval,"buzz","friendships");
    MessageDBRepo mrepo = new MessageDBRepo("buzz");
    Service service = new Service(urepo,frepo,mrepo);
    LastUserLogged lastUserLogged = LastUserLogged.getInstance();


    @FXML
    private Label resultText;
    @FXML
    private TextField nameTF;
    @FXML
    private TextField usernameTF;
    @FXML
    private PasswordField passwordTF;
    @FXML
    private PasswordField confirmpasswordTF;
    @FXML
    private TextField emailTF;
    @FXML
    private ImageView buzzImage;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        load_images();
    }
    public void load_images(){
        String working_directory = System.getProperty("user.dir");
        Image buzz = new Image(working_directory+"\\images\\buzz.jpg");
        buzzImage.setImage(buzz);
    }

    @FXML
    protected void onRegisterButtonClick(){
        if(Objects.equals(nameTF.getText(), "") || Objects.equals(usernameTF.getText(), "") || Objects.equals(passwordTF.getText(), "") || Objects.equals(confirmpasswordTF.getText(), "") || Objects.equals(emailTF.getText(), "")){
            resultText.setText("Please complete all fields!");
            return;
        }
        if(!Objects.equals(passwordTF.getText(),confirmpasswordTF.getText())){
            resultText.setText("Passwords do not match!");
            return;
        }
        for(User user: service.allUsersS()){
            if (Objects.equals(user.getUsername(), usernameTF.getText())){
                resultText.setText("This username already exists!");
                return;
            }
            if(Objects.equals(user.getEmail(), emailTF.getText())){
                resultText.setText("This email is registered!");
                return;
            }
        }
        try {
            service.addUserS(nameTF.getText(),usernameTF.getText(),passwordTF.getText(),emailTF.getText());
            resultText.setText("Register successful!");
        }
        catch (Exception e){
            resultText.setText(e.getMessage());
        }
    }
}
