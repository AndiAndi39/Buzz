package com.example.reteasocializare;

import domain.Message;
import domain.validator.FriendshipValidator;
import domain.validator.UserValidator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import observer.MyObservable;
import observer.MyObserver;
import repository.FriendshipDBRepo;
import repository.MessageDBRepo;
import repository.UserDBRepo;
import service.Service;
import utils.LastUserLogged;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class UsermainController implements Initializable, MyObserver {

    UserValidator uval = new UserValidator();
    UserDBRepo urepo  = new UserDBRepo(uval, "buzz","users");
    FriendshipValidator fval = new FriendshipValidator();
    FriendshipDBRepo frepo = new FriendshipDBRepo(fval,"buzz","friendships");
    MessageDBRepo mrepo = new MessageDBRepo("buzz");
    Service service = new Service(urepo,frepo,mrepo);
    MyObservable myObservable = MyObservable.getInstance();

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
    private TextArea chatMsg;

    private Stage currentStage;


    @Override
    public void initialize(URL url, ResourceBundle rb){
        myObservable.addObserver(this);
        LastUserLogged lastUserLogged = LastUserLogged.getInstance();
        service.setCurrentUser(lastUserLogged.getUsername());
        this.currentStage = lastUserLogged.getLastStage();
        update();

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
    }

    public void load_messages(){
        chatTextFlow.getChildren().clear();
        List<Message> msgs = service.get_messages(service.getCurrentUser(),currentChatUser);
        msgs.sort(new Comparator<Message>() {
            public int compare(Message m1, Message m2) {
                return m1.getTime().isBefore(m2.getTime()) ? 1 : 0;
            }
        });
        for(Message message: msgs){
            Text t = null;
            if(Objects.equals(message.getFrom(), service.getCurrentUser())){
                t = new Text("YOU: "+message.getText()+"\n");
            }
            else{
                t = new Text(currentChatUser+": "+message.getText()+"\n");
            }
            chatTextFlow.getChildren().add(t);
        }
        try{
            Message last_m = msgs.get(msgs.size()-1);
            String musicFile = "StayTheNight.mp3";     // For example

            if(Objects.equals(last_m.getText(), "BUZZ!!!") && Objects.equals(last_m.getTo(), service.getCurrentUser())){
                Timeline timelineX = new Timeline(new KeyFrame(Duration.seconds(0.05), new EventHandler<ActionEvent>() {
                    int x=0;
                    @Override
                    public void handle(ActionEvent t) {
                        if (x == 0) {
                            currentStage.setX(currentStage.getX() + 10);
                            x = 1;
                        } else {
                            currentStage.setX(currentStage.getX() - 10);
                            x = 0;
                        }
                    }
                }));
                timelineX.setCycleCount(5);
                timelineX.setAutoReverse(false);
                timelineX.play();
            }
        }
        catch (Exception e){

        }
    }
    @FXML
    public void sendBuzz(){
        Message message = new Message(service.getCurrentUser(),currentChatUser,"BUZZ!!!", LocalDateTime.now());
        mrepo.save(message);
        Text t = new Text("YOU: "+"BUZZ!!!"+"\n");
        chatTextFlow.getChildren().add(t);
        myObservable.obs_notify();
    }
    @FXML
    public void sendMsg(){
        String text = chatMsg.getText();
        Message message = new Message(service.getCurrentUser(),currentChatUser,text, LocalDateTime.now());
        mrepo.save(message);
        Text t = new Text("YOU: "+text+"\n");
        chatTextFlow.getChildren().add(t);
        myObservable.obs_notify();
    }

    @FXML
    public void load_friends(){
        prieteniList.getItems().clear();
        Iterable<String> friends = service.userFriends(service.getCurrentUser());
        for(String friend: friends){
            prieteniList.getItems().add(friend);
        }
    }

    @Override
    public void update(){
        load_friends();
        load_requests();
        load_messages();
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
        myObservable.obs_notify();
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
        myObservable.obs_notify();
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
        myObservable.obs_notify();
    }

    @FXML
    protected void deleteR(){
        Pair<String,String> item = cereriList.getSelectionModel().getSelectedItem();
        String username = item.getKey();
        service.removeFriendshipS(username,service.getCurrentUser());
        myObservable.obs_notify();
    }

}
