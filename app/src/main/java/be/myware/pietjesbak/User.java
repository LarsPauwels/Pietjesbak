package be.myware.pietjesbak;

public class User {

    public String username, email;
    public int wins, plays;

    public User() {

    }

    public User(String username, String email, int wins, int plays) {
        this.username = username;
        this.email = email;
        this.wins = wins;
        this.plays = plays;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
