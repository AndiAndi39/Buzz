package utils;

public class LastUserLogged {

    private static LastUserLogged single_instance = null;
    private static String username;

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
