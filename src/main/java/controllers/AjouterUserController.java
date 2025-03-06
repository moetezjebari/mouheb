package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;
import services.EmailService;  // Assurez-vous d'importer votre service d'email

import java.io.IOException;  // Importation de IOException

public class AjouterUserController {

    @FXML
    private TextField TFnom;

    @FXML
    private TextField TFprenom;

    @FXML
    private TextField TFemail;

    @FXML
    private PasswordField TFmotDePasse;

    @FXML
    private TextField TFtelephone;  // Ajout du champ pour le téléphone

    @FXML
    private RadioButton rbFreelance;

    @FXML
    private RadioButton rbFormateur;

    @FXML
    private RadioButton rbEmployeur;

    @FXML
    private RadioButton rbAdmin;

    private final UtilisateurService serviceUtilisateur = new UtilisateurService();

    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        rbFreelance.setToggleGroup(group);
        rbFormateur.setToggleGroup(group);
        rbEmployeur.setToggleGroup(group);
        rbAdmin.setToggleGroup(group);
    }

    @FXML
    private void ajouterUtilisateur(ActionEvent event) {
        String nom = TFnom.getText().trim();
        String prenom = TFprenom.getText().trim();
        String email = TFemail.getText().trim();
        String motDePasse = TFmotDePasse.getText().trim();
        String telephone = TFtelephone.getText().trim();  // Récupération du téléphone
        String typeUtilisateur = getTypeUtilisateurSelectionne();

        // Vérification des champs
        if (typeUtilisateur == null) {
            showAlert("Erreur", "Veuillez sélectionner un type d'utilisateur.");
            return;
        }

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || telephone.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Vérification de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Erreur", "Email invalide !");
            return;
        }

        // Vérification du mot de passe
        if (motDePasse.length() < 8) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        // Création de l'utilisateur
        Utilisateur utilisateur = new Utilisateur(nom, prenom, email, motDePasse, telephone, typeUtilisateur);

        // Ajout de l'utilisateur via le service
        if (serviceUtilisateur.ajouterUtilisateur(utilisateur)) {
            showAlert("Succès", "Utilisateur ajouté avec succès !");

            // Envoi de l'email de bienvenue
            try {
                EmailService.sendWelcomeEmail(email);  // Envoie l'email de bienvenue
            } catch (Exception e) {  // Capturer toute exception générale
                e.printStackTrace();  // Affiche l'exception dans la console
                showAlert("Erreur", "Une erreur est survenue lors de l'envoi de l'email.");
            }

            // Rafraîchir automatiquement le Dashboard
            if (DashboardController.getInstance() != null) {
                DashboardController.getInstance().refreshUserList();
            }

            // Fermer la fenêtre après l'ajout
            fermerFenetre();
        } else {
            showAlert("Erreur", "Échec de l'ajout de l'utilisateur.");
        }
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) TFnom.getScene().getWindow();
        stage.close();
    }

    private String getTypeUtilisateurSelectionne() {
        if (rbFreelance.isSelected()) return "Freelance";
        if (rbFormateur.isSelected()) return "Formateur";
        if (rbEmployeur.isSelected()) return "Employeur";
        if (rbAdmin.isSelected()) return "Admin";
        return "Utilisateur"; // Assure-toi qu'une valeur est toujours retournée
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
