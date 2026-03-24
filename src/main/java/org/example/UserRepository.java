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
    public boolean register(String login, String password) throws IOException {
        if(getUser(login) != null){
            System.out.println("Uzytkownik o tym loginie juz istnieje");
            return false;
        }
        String hashedPassword = Authentication.hashPassword(password);
        User newUser = new User(login, hashedPassword, "USER", "null");
        users.add(newUser);
        save();
        System.out.println("Nowy uzytkownik został dodany.");
        return true;
    }

    @Override
    public boolean removeUser(String login) throws IOException {
        User user = getUser(login);

        if(user==null){
            System.out.println("Uzytkownik " + login + " nie istnieje");
            return false;
        }

        if(user.getRentedVehicleId() != null && !user.getRentedVehicleId().equals("null")){
            System.out.println("Nie mozna usunac uzytkownika " + login + ", poniewaz posiada wypozyczony pojazd");
            return false;
        }

        boolean removed = users.removeIf(u -> u.getLogin().equals(login));
        if(removed) {
            save();
            System.out.println("Uzytkownik " + login + " zostal usuniety");
        }
        return removed;
    }


    private void save() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))){
            for (User u : users){
                writer.println(u.getLogin() + ";" + u.getPassword() + ";" + u.getRole() + ";" + u.getRentedVehicleId());
            }
        }catch (IOException e){
            System.err.println("Blad zapisu uzytkownikow: " + e.getMessage());
        }
    }

    private void load() throws FileNotFoundException {
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
