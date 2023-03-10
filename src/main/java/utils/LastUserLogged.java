package utils;

import javafx.stage.Stage;

public class LastUserLogged {

    private static LastUserLogged single_instance = null;
    private static String username;

    private static Stage lastStage;

    public Stage getLastStage() {
        return lastStage;
    }

    public void setLastStage(Stage stage) {
        lastStage = stage;
    }

    public static LastUserLogged getInstance(){
        if(single_instance == null){
            single_instance = new LastUserLogged();
        }
        return single_instance;
    }
    private LastUserLogged(){

    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String new_username){
        username = new_username;
    }

}
