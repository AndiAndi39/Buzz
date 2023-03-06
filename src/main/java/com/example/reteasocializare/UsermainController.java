package com.example.reteasocializare;

import domain.Message;
import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import repository.FriendshipDBRepo;
import repository.MessageDBRepo;
import repository.UserDBRepo;
import service.Service;
import utils.LastUserLogged;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class UsermainController implements Initializable {

    UserValidator uval = new UserValidator();
    UserDBRepo urepo  = new UserDBRepo(uval, "buzz","users");
    FriendshipValidator fval = new FriendshipValidator();
    FriendshipDBRepo frepo = new FriendshipDBRepo(fval,"buzz","friendships");
    MessageDBRepo mrepo = new MessageDBRepo("buzz");
    Service service = new Service(urepo,frepo,mrepo);

    private String currentUser;

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUser(){
        return currentUser;
    }

    @FXML
    private ListView<String> prieteniList;
    @FXML
    private TextField newFriend;
    @FXML
    private Label resultLabel;
    @FXML
    private TableView<Pair<String, String>> cereriList;
    @FXML
    private TableColumn<Pair<String, String>,String> userColumn;
    @FXML
    private TableColumn<Pair<String, String>,String> statusColumn;

    private String currentChatUser;

    @FXML
    private TextFlow chatTextFlow;
    @FXML
    private Button sendMsgBtn;
    @FXML
    private TextArea chatMsg;


    @Override
    public void initialize(URL url, ResourceBundle rb){
        LastUserLogged lastUserLogged = LastUserLogged.getInstance();
        service.setCurrentUser(lastUserLogged.getUsername());

        load_friends();
        load_requests();

        prieteniList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    ObservableList<String> selected = prieteniList.getSelectionModel().getSelectedItems();
                    currentChatUser = selected.get(0);
                    load_messages();
                }
            }
        });

        System.out.println(service.getCurrentUser());
    }

    public void load_messages(){
        chatTextFlow.getChildren().clear();
        List<String> msgs = service.get_messages(service.getCurrentUser(),currentChatUser);
        for(String s: msgs){
            Text t = new Text(s+"\n");
            chatTextFlow.getChildren().add(t);
        }
    }
    @FXML
    public void sendMsg(){
        String text = chatMsg.getText();
        Message message = new Message(service.getCurrentUser(),currentChatUser,text, LocalDateTime.now());
        mrepo.save(message);
        Text t = new Text(text);
        chatTextFlow.getChildren().add(t);
    }

    @FXML
    public void load_friends(){
        prieteniList.getItems().clear();
        Iterable<String> friends = service.userFriends(service.getCurrentUser());
        for(String friend: friends){
            prieteniList.getItems().add(friend);
        }
    }

    @FXML
    public void load_requests(){
        cereriList.getItems().clear();
        userColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        statusColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));

        Iterable<Pair<String,String>> requests = service.userRequests(service.getCurrentUser());
        for(Pair<String,String> request: requests){
            cereriList.getItems().add(request);
        }
    }

    @FXML
    protected void deleteFriend(){
        ObservableList<String> selected = prieteniList.getSelectionModel().getSelectedItems();
        String friend = selected.get(0);
        service.removeFriendshipS(friend,service.getCurrentUser());
        load_friends();
    }

    @FXML
    protected void addFriend(){
        String newfriend = newFriend.getText();
        try{
            service.addFriendshipS(service.getCurrentUser(),newfriend);
        }
        catch (Exception e){
            resultLabel.setText(e.getMessage());
        }
        load_friends();
        load_requests();
    }

    @FXML
    protected void acceptR(){
        Pair<String,String> request = cereriList.getSelectionModel().getSelectedItem();
        try{
            service.acceptFriendshipS(service.getCurrentUser(),request);
        }
        catch (Exception e){
            resultLabel.setText(e.getMessage());
        }
        load_requests();
        load_friends();
    }

    @FXML
    protected void deleteR(){
        Pair<String,String> item = cereriList.getSelectionModel().getSelectedItem();
        String username = item.getKey();
        service.removeFriendshipS(username,service.getCurrentUser());
        load_requests();
    }
}
