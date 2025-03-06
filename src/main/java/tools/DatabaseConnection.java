package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe Singleton pour gérer la connexion à la base de données MySQL.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pifreelance"; // Nom de ta BD
    private static final String USER = "root"; // Utilisateur MySQL
    private static final String PWD = ""; // Mets ton mot de passe si nécessaire

    private static Connection cnx;
    private static DatabaseConnection instance;

    /**
     * Constructeur privé (Singleton) - Évite l'instanciation multiple.
     */
    private DatabaseConnection() {
        try {
            // Charger le driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir une seule connexion et la garder ouverte
            cnx = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("✅ Connexion établie avec succès !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Erreur : Driver JDBC non trouvé !");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données !");
            e.printStackTrace();
        }
    }

    /**
     * Retourne l'instance unique (Singleton)
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Retourne la connexion active à la base de données.
     */
    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) { // Vérifie si la connexion est fermée et la réouvre si nécessaire
                cnx = DriverManager.getConnection(URL, USER, PWD);
                System.out.println("🔄 Réouverture de la connexion MySQL...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnx;
    }
}
