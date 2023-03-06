package com.example.reteasocializare;

import domain.User;
import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

public class RegisterController {
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
    protected void onRegisterButtonClick(){
        if(Objects.equals(nameTF.getText(), "") || Objects.equals(usernameTF.getText(), "") || Objects.equals(passwordTF.getText(), "") || Objects.equals(confirmpasswordTF.getText(), "") || Objects.equals(emailTF.getText(), "")){
            resultText.setText("Toate campurile sunt obligatorii!");
            return;
        }
        if(!Objects.equals(passwordTF.getText(),confirmpasswordTF.getText())){
            resultText.setText("Parolele nu coincid!");
            return;
        }
        try {
            service.addUserS(nameTF.getText(),usernameTF.getText(),passwordTF.getText(),emailTF.getText());
            resultText.setText("Inregistrare cu succes!");
        }
        catch (Exception e){
            resultText.setText(e.toString());
        }
    }
}
