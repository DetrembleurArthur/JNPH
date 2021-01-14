package parsers;

public class Database implements ObjectParser{
    public String nom;
    public String user;
    public String password;

    public Database(String nom, String user, String password) {
        this.nom = nom;
        this.user = user;
        this.password = password;
    }

    public Database(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public Database() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Database{" +
                "nom='" + nom + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
