

package com.example.reteasocializare;

import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.FriendshipDBRepo;
import repository.MessageDBRepo;
import repository.UserDBRepo;
import service.Service;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        UserValidator uval = new UserValidator();
        UserDBRepo urepo  = new UserDBRepo(uval, "buzz","users");
        FriendshipValidator fval = new FriendshipValidator();
        FriendshipDBRepo frepo = new FriendshipDBRepo(fval,"buzz","friendships");
        MessageDBRepo mrepo = new MessageDBRepo("buzz");
        Service service = Service.getInstance(urepo,frepo,mrepo);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
