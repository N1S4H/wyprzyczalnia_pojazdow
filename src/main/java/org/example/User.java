package org.example;

public class User {
    private String login;
    private String password;
    private String role;
    private String rentedVehicleId;

    public User(String login, String password, String role, String rentedVehicleId) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;
    }

    public User(User other){
        this.login = other.login;
        this.password = other.password;
        this.role = other.role;
        this.rentedVehicleId = other.rentedVehicleId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getRentedVehicleId() {
        return rentedVehicleId;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setRentedVehicleId(String rentedVehicleId) {
        this.rentedVehicleId = rentedVehicleId;
    }
}
