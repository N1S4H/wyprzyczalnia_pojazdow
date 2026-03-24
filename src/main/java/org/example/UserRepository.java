package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserRepository implements IUserRepository{
    private List<User> users = new ArrayList<>();
    private final String fileName = "users.csv";

    public UserRepository() throws FileNotFoundException {
        load();
    }

    @Override
    public User getUser(String login) {
        for (User u : users){
            if (u.getLogin().equals(login)){
                return new User(u);
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> copy = new ArrayList<>();
        for(User u : users){
            copy.add(new User(u));
        }
        return copy;
    }

    @Override
    public boolean update(User user) throws IOException {
        for(int i = 0; i<users.size(); i++){
            if(users.get(i).getLogin().equals(user.getLogin())){
                users.set(i, new User(user));
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public void save() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))){
            for (User u : users){
                writer.println(u.getLogin() + ";" + u.getPassword() + ";" + u.getRole() + ";" + u.getRentedVehicleId());
            }
        }catch (IOException e){
            System.err.println("Blad zapisu uzytkownikow: " + e.getMessage());
        }
    }

    @Override
    public void load() throws FileNotFoundException {
        users.clear();
        File file = new File(fileName);
        if (!file.exists()){
            return;
        }

        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if (line.isEmpty()){
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length < 4) {
                    System.err.println("DEBUG: Linia w pliku ma za malo danych: " + line);
                    continue;
                }
                users.add(new User(parts[0], parts[1], parts[2], parts[3]));
            }
        }catch (Exception e){
            System.err.println("Blad wczytywania użytkownikow: " + e.getMessage());
        }
    }
}
