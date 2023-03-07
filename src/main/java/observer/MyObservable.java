package observer;

import java.util.ArrayList;
import java.util.List;

public class MyObservable {
    private static MyObservable singleInstance;

    public static MyObservable getInstance(){
        if(singleInstance == null){
            singleInstance = new MyObservable();
        }
        return singleInstance;
    }
    private MyObservable(){

    }
    private List<MyObserver> all_obs = new ArrayList<>();

    public void addObserver(MyObserver obs) {
        this.all_obs.add(obs);
    }

    public void removeObserver(MyObserver obs) {
        this.all_obs.remove(obs);
    }

    public void obs_notify() {
        for (MyObserver obs : this.all_obs) {
            obs.update();
        }
    }
}
