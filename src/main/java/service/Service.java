package service;

import domain.Friendship;
import domain.Message;
import domain.User;
import javafx.util.Pair;
import repository.MessageDBRepo;
import repository.Repository;
import utils.myFunction;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Service{
    private String curentUser;

    public void setCurentUser(String curentUser) {
        this.curentUser = curentUser;
    }
    public String getCurentUser(){
        return curentUser;
    }

    private static Service singletonService;
    private final Repository<User, String> userRepo;
    private final Repository<Friendship, Pair<String, String>> friendshipRepo;

    private final MessageDBRepo messageRepo;

    private Service(Repository<User, String>r1, Repository<Friendship, Pair<String, String>> r2,MessageDBRepo r3){
        this.userRepo = r1;
        this.friendshipRepo = r2;
        this.messageRepo = r3;
    }

    public static Service getInstance(Repository<User, String>r1, Repository<Friendship, Pair<String, String>> r2, MessageDBRepo r3){
        if(singletonService == null){
            singletonService = new Service(r1,r2,r3);
        }

        return singletonService;
    }

    public void addUserS(String name, String username, String pass, String email){
        String salt = myFunction.generateRandomWords(1)[0];
        try
        {
            pass = myFunction.toHexString(myFunction.getSHA(pass+salt));
        }
        catch (NoSuchAlgorithmException e) {
            //System.out.println("eroare!");
        }
        User user = new User(name,username,pass,email,salt);
        user.setId(username);
        userRepo.save(user);
    }

    public List<String> get_messages(String from, String to){
        Iterable<Message> all_msgs = messageRepo.findAll();
        List<String> msgs = new ArrayList<>();
        for(Message m : all_msgs){
            if(Objects.equals(m.getFrom(), from) && Objects.equals(m.getTo(), to)){
                msgs.add(m.getText());
            }
        }
        return msgs;
    }

    public void removeUserS(String username){
        User deleted = (User) userRepo.findOne(username);
        if(deleted!=null)
        {
            Iterable<Friendship> friendships = allFriendships();
            List<Pair<String,String>> deleted_friendships_id = new ArrayList<Pair<String,String>>();
            for(Friendship f : friendships){
                if(Objects.equals(f.getUser1(), deleted.getUsername())){
                    deleted_friendships_id.add(new Pair<String,String>(f.getUser1(),f.getUser2()));
                }
                else if(Objects.equals(f.getUser2(), deleted.getUsername())){
                    deleted_friendships_id.add(new Pair<String,String>(f.getUser1(),f.getUser2()));
                }

            }

            //delete all the friendships for user
            for(Pair<String,String> id: deleted_friendships_id){
                friendshipRepo.delete(id);
            }
        }
        userRepo.delete(username);
    }

    public void updateUserS(String username, String new_password){
        User old_user = userRepo.findOne(username);
        String salt = myFunction.generateRandomWords(1)[0];
        try
        {
            new_password = myFunction.toHexString(myFunction.getSHA(new_password+salt));
        }
        catch (NoSuchAlgorithmException e) {
            //ceva de adaugat
        }
        User new_user = new User(old_user.getName(), old_user.getUsername(),new_password, old_user.getEmail(),salt);
        new_user.setId(old_user.getUsername());
        userRepo.update(new_user, old_user);
    }

    public Iterable<User> allUsersS(){
        return userRepo.findAll();
    }

    /*
    Returns all friends for a user
    return type: Iterable<String>
     */
    public Iterable<String> userFriends(String username){
        User user = userRepo.findOne(username);
        Iterable<Friendship> all_friendships = friendshipRepo.findAll();
        List<String> user_friends = new ArrayList<String>();
        all_friendships.forEach(
                x->{
                    if(Objects.equals(x.getUser2(), user.getUsername()) && Objects.equals(x.getStatus(), "confirmed")){
                        user_friends.add(x.getUser1());
                    }
                    if(Objects.equals(x.getUser1(), user.getUsername())  && Objects.equals(x.getStatus(), "confirmed")){
                        user_friends.add(x.getUser2());
                    }
                }
        );
        return user_friends;
    }

    public Iterable<Pair<String,String>> userRequests(String username){
        User user = userRepo.findOne(username);
        Iterable<Friendship> all_friendships = friendshipRepo.findAll();
        List<Pair<String,String>> user_requests = new ArrayList<Pair<String,String>>();
        all_friendships.forEach(
                x->{
                    if(Objects.equals(x.getUser2(), user.getUsername()) && Objects.equals(x.getStatus(), "pending")){
                        user_requests.add(new Pair<String,String>(x.getUser1(),"received"));
                    }
                    if(Objects.equals(x.getUser1(), user.getUsername())  && Objects.equals(x.getStatus(), "pending")){
                        user_requests.add(new Pair<String,String>(x.getUser2(),"sent"));
                    }
                }
        );
        return user_requests;
    }

    /*
    ! nu e gata
     */
    public void addFriendshipS(String username1, String username2) throws Exception {
        if(Objects.equals(username1, username2)) {
            throw new Exception("Cu acest username esti conectat!");
        }
        if(userRepo.findOne(username1) == null || userRepo.findOne(username2)  == null){
            throw new Exception("Username-ul nu exista!");
        }
        Pair<String, String> check1 = new Pair<>(username1,username2);
        Pair<String, String> check2 = new Pair<>(username2,username1);

        if(friendshipRepo.findOne(check1) != null || friendshipRepo.findOne(check2) != null){
            throw new Exception("Este trimisa o cerere sau sunteti prieteni!");
        }
        Friendship friendship = new Friendship(username1,username2,"pending");
        friendship.setId(new Pair<String,String>(username1,username2));
        friendshipRepo.save(friendship);
    }

    public void removeFriendshipS(String username1,String username2){
        friendshipRepo.delete(new Pair<>(username1,username2));
        friendshipRepo.delete(new Pair<>(username2,username1));
    }

    public void acceptFriendshipS(String cUser, Pair<String,String> request) throws Exception{
        if(request.getValue() == "sent"){
            throw new Exception("Ai trimis cerere de prietenie");
        }
        String sender = request.getKey();
        Friendship oldF = new Friendship(sender,cUser,"pending");
        oldF.setId(new Pair<String,String>(sender,cUser));
        Friendship newF = new Friendship(sender,cUser,"confirmed");
        friendshipRepo.update(newF,oldF);
    }

    public Iterable<Friendship> allFriendships(){
        return friendshipRepo.findAll();
    }

    /*public int nrCS(){

        //Creare graf
        Map<String, Integer> nr_user = new HashMap<>();
        Map<Integer, List<Integer>> graph = new HashMap<>();
        int nodesNR=0;
        for(User u : allUsersS()){
            nodesNR++;
            nr_user.put(u.getUsername(),nodesNR);
            graph.put(nodesNR,new ArrayList<>());
        }
        for(Friendship f : allFriendships()){
            int u1 = nr_user.get(f.getUser1().getUsername());
            int u2 = nr_user.get(f.getUser2().getUsername());
            graph.get(u1).add(u2);
            graph.get(u2).add(u1);
        }

        //Componente conexe
        int nrc=0;
        int visited[] = new int[nodesNR+10];
        for(int i=1;i<=nodesNR;i++){
            if(visited[i] == 0){
                dfs(i,visited,graph);
                nrc++;
            }
        }


        return nrc;
    }

    public List<String> bestCS(){
        //Creare graf
        Map<String, Integer> nr_user = new HashMap<>();
        Map<Integer, List<Integer>> graph = new HashMap<>();
        int nodesNR=0;
        for(User u : allUsersS()){
            nodesNR++;
            nr_user.put(u.getUsername(),nodesNR);
            graph.put(nodesNR,new ArrayList<>());
        }
        for(Friendship f : allFriendships()){
            int u1 = nr_user.get(f.getUser1().getUsername());
            int u2 = nr_user.get(f.getUser2().getUsername());
            graph.get(u1).add(u2);
            graph.get(u2).add(u1);
        }

        int maxim=0;
        int parent=0;
        for(int i=1;i<=nodesNR;i++){

            maxim = Math.max(BFS(i,nodesNR,graph),maxim);
            parent = i;
        }

        int visited[] = new int[nodesNR+10];

        dfs(parent,visited,graph);
        List<String> comunitate = new ArrayList<>();
        for(User u:allUsersS()){
            if(visited[nr_user.get(u.getUsername())] == 1){
                comunitate.add(u.getUsername());
            }
        }
        return comunitate;
    }
    private int BFS(int s,int nr,Map<Integer, List<Integer>> graph) {
        // Mark all the vertices as not visited(By default
        // set as false)
        int dist[] = new int[nr + 1];
        boolean visited[] = new boolean[nr + 1];

        // Create a queue for BFS
        LinkedList<Integer> queue = new LinkedList<Integer>();

        // Mark the current node as visited and enqueue it
        visited[s] = true;
        queue.add(s);

        while (queue.size() != 0) {
            // Dequeue a vertex from queue and print it
            s = queue.poll();

            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            List<Integer> i = graph.get(s);
            for (int n : i) {
                if (!visited[n]) {
                    visited[n] = true;
                    dist[n] = dist[s] + 1;
                    queue.add(n);
                }
            }
        }
        int maxim=0;
        for(int i=1;i<=nr;i++){
            maxim = Math.max(maxim,dist[i]);
        }
        return maxim;
    }
    private void dfs(int node, int visited[],Map<Integer, List<Integer>> graph){
        visited[node] = 1;
        for(int i=0;i<graph.get(node).size();i++){
            int n = graph.get(node).get(i);
            if(visited[n] == 0){
                dfs(n,visited,graph);
            }
        }
    }*/

}
