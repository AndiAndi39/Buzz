package com.example.reteasocializare;

import domain.User;
import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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


public class LoginController implements Initializable {

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
    private TextField usernameTF = new TextField();
    @FXML
    private PasswordField passwordTF = new PasswordField();

    @FXML
    private ImageView leftImage;

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
        Image exclamation = new Image(working_directory+"\\images\\exclamation.jpg");
        leftImage.setImage(exclamation);
    }

    @FXML
    protected void onLoginButtonClick() {
        if(Objects.equals(usernameTF.getText(), "") || Objects.equals(passwordTF.getText(), ""))
        {
            resultText.setText("The fields must not be empty");
            return;
        }
        User user = urepo.findOne(usernameTF.getText());
        if(user == null){
            resultText.setText("Incorrect password or username!");
            return;
        }
        String password = passwordTF.getText()+user.getSalt();
        try {
            password = myFunction.toHexString(myFunction.getSHA(password));
        }
        catch (NoSuchAlgorithmException e){

        }
        if(password.equals(user.getPassword())){
            resultText.setText("Login successful!");
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("usermain.fxml"));
                lastUserLogged.setUsername(usernameTF.getText());
                Stage stage = new Stage();
                lastUserLogged.setLastStage(stage);
                Scene scene = new Scene(fxmlLoader.load(), 850, 500);
                stage.setTitle(user.getUsername());
                stage.setScene(scene);
                stage.show();
            }
            catch (IOException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create new Window.", e);
            }
        }
        else{
            resultText.setText("Incorrect password or username!");
        }
    }

    @FXML
    protected void noaccountTextClick(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 480);
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
}