package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;
import tools.DatabaseConnection;
import tools.SessionManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SignInController {

    @FXML
    private TextField TFemail;

    @FXML
    private PasswordField TFmotDePasse;

    private final UtilisateurService serviceUtilisateur = new UtilisateurService();

    /**
     * ✅ Action exécutée lors de la connexion
     */
    @FXML
    private void signInAction(ActionEvent event) {
        String email = TFemail.getText();
        String motDePasse = TFmotDePasse.getText();

        // ✅ Vérification des champs vides
        if (email.isEmpty() || motDePasse.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // ✅ Vérification de l'email
        if (!isValidEmail(email)) {
            showAlert("Erreur", "L'adresse email est invalide.");
            return;
        }

        // 🔥 Vérification de l'admin principal (Hardcoded)
        if (email.equals("admin@root.com") && motDePasse.equals("root1234")) {
            showAlert("Succès", "Connexion réussie en tant qu'Admin Principal !");

            // 🔥 Stocker un utilisateur fictif pour la session
            Utilisateur adminRoot = new Utilisateur(0, "Admin", "Root", email, "Admin", "", new Timestamp(System.currentTimeMillis()));
            SessionManager.getInstance().setLoggedInUser(adminRoot);

            // 🔥 Redirection vers le Dashboard Admin
            openWindow("/Dashboard.fxml", "Tableau de Bord - Admin");

            // 🔥 Fermer la fenêtre actuelle (SignIn)
            Stage currentStage = (Stage) TFemail.getScene().getWindow();
            currentStage.close();
            return;
        }

        // 🔥 Vérification des identifiants en base de données
        Utilisateur utilisateur = authenticateUser(email, motDePasse);

        if (utilisateur != null) {
            // 🔥 Stocker l'utilisateur dans la session
            SessionManager.getInstance().setLoggedInUser(utilisateur);

            showAlert("Succès", "Connexion réussie !");
            redirectUser(utilisateur.getTypeUtilisateur()); // 🔥 Redirection selon le type d'utilisateur

            // 🔥 Fermer la fenêtre actuelle (SignIn)
            Stage currentStage = (Stage) TFemail.getScene().getWindow();
            currentStage.close();
        } else {
            showAlert("Erreur", "Email ou mot de passe incorrect.");
        }
    }

    /**
     * ✅ Vérifie si l'email est valide
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    /**
     * 🔥 Vérifie si l'utilisateur existe et récupère son rôle
     */
    private Utilisateur authenticateUser(String email, String password) {
        String query = "SELECT id, nom, prenom, email, type_utilisateur FROM utilisateur WHERE email = ? AND mot_de_passe = SHA2(?, 256)";

        try (Connection conn = DatabaseConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("type_utilisateur"),
                        rs.getString("telephone"),  // Ajoutez la récupération du téléphone si possible
                        rs.getTimestamp("date_inscription")  // Ajoutez la récupération de la date d'inscription
                );
            }

        } catch (SQLException e) {
            showAlert("Erreur SQL", "Problème de connexion à la base de données.");
            e.printStackTrace();
        }
        return null; // Aucun utilisateur trouvé
    }

    /**
     * 🔥 Redirige l'utilisateur selon son rôle
     */
    private void redirectUser(String typeUtilisateur) {
        if ("Admin".equalsIgnoreCase(typeUtilisateur)) {
            openWindow("/Dashboard.fxml", "Tableau de Bord - Admin");
        } else {
            openWindow("/HomePage.fxml", "Page d'Accueil");
        }
    }

    /**
     * ✅ Ouvre une nouvelle fenêtre
     */
    private void openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la page " + title);
            e.printStackTrace();
        }
    }

    /**
     * ✅ Affiche une boîte d'alerte
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * ✅ Action pour le bouton "Mot de passe oublié".
     * 🔥 Redirige vers la page de récupération de mot de passe.
     */
    @FXML
    private void forgotPasswordAction(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page "ModePass.fxml"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modepasse.fxml"));
            Parent root = loader.load();

            // Obtenez la scène de la fenêtre actuelle (SignIn)
            Stage currentStage = (Stage) TFemail.getScene().getWindow();

            // Remplacez le contenu de la scène actuelle avec la page de récupération du mot de passe
            currentStage.getScene().setRoot(root);

        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de récupération de mot de passe.");
            e.printStackTrace();
        }
    }

    /**
     * ✅ Action pour le bouton "Créer un compte".
     * 🔥 Redirige vers la page d'inscription dans la même fenêtre.
     */
    @FXML
    private void signUpAction(ActionEvent event) {
        try {
            // Charger le fichier FXML de la page d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
            Parent root = loader.load();

            // Obtenez la scène de la fenêtre actuelle (SignIn)
            Stage currentStage = (Stage) TFemail.getScene().getWindow();

            // Remplacez le contenu de la scène actuelle avec la scène d'inscription
            currentStage.getScene().setRoot(root);

        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page d'inscription.");
            e.printStackTrace();
        }
    }
}
