package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import models.Utilisateur;
import services.UtilisateurService;

public class SignUpController {

    @FXML
    private TextField TFnom;

    @FXML
    private TextField TFprenom;

    @FXML
    private TextField TFemail;

    @FXML
    private PasswordField TFmotDePasse;

    @FXML
    private TextField TFtelephone;  // Ajout du champ pour le numéro de téléphone

    @FXML
    private RadioButton rbFreelance;

    @FXML
    private RadioButton rbFormateur;

    @FXML
    private RadioButton rbEmployeur;

    private final UtilisateurService serviceUtilisateur = new UtilisateurService();

    @FXML
    public void initialize() {
        // Associer les boutons radio à un seul groupe pour assurer une sélection unique
        ToggleGroup group = new ToggleGroup();
        rbFreelance.setToggleGroup(group);
        rbFormateur.setToggleGroup(group);
        rbEmployeur.setToggleGroup(group);
    }

    @FXML
    private void signUpAction(ActionEvent event) {
        String nom = TFnom.getText().trim();
        String prenom = TFprenom.getText().trim();
        String email = TFemail.getText().trim();
        String motDePasse = TFmotDePasse.getText().trim();
        String telephone = TFtelephone.getText().trim();  // Capture du numéro de téléphone

        // Vérifier qu'un seul bouton radio est sélectionné
        String typeUtilisateur = null;
        if (rbFreelance.isSelected()) {
            typeUtilisateur = "Freelance";
        } else if (rbFormateur.isSelected()) {
            typeUtilisateur = "Formateur";
        } else if (rbEmployeur.isSelected()) {
            typeUtilisateur = "Employeur";
        }

        if (typeUtilisateur == null) {
            showAlert("Erreur", "Veuillez sélectionner un type d'utilisateur.");
            return;
        }

        // Vérifier si tous les champs sont remplis
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || telephone.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Vérifier la validité de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Erreur", "Email invalide !");
            return;
        }

        // Vérifier la validité du numéro de téléphone (exemple simple)
        if (!telephone.matches("\\d{10}")) {  // Assurez-vous qu'il a 10 chiffres
            showAlert("Erreur", "Numéro de téléphone invalide !");
            return;
        }

        // Vérifier la longueur du mot de passe
        if (motDePasse.length() < 8) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        // Créer un utilisateur avec le numéro de téléphone
        Utilisateur utilisateur = new Utilisateur(nom, prenom, email, motDePasse, telephone, typeUtilisateur);
        if (serviceUtilisateur.ajouterUtilisateur(utilisateur)) {
            showSuccessMessage("Succès", "Utilisateur ajouté avec succès !");
        } else {
            showAlert("Erreur", "Échec de l'inscription.");
        }
    }

    private void showSuccessMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType btnSignIn = new ButtonType("Se connecter");
        ButtonType btnStay = new ButtonType("Rester ici", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnSignIn, btnStay);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnSignIn) {
                goToSignIn();  // Redirection vers la page de connexion
            }
        });
    }

    /**
     * Redirige vers la page de connexion dans la même fenêtre
     */
    @FXML
    private void goToSignIn() {
        try {
            // Charger le fichier FXML de la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
            Parent root = loader.load();

            // Obtenez la scène de la fenêtre actuelle (SignUp)
            Stage currentStage = (Stage) TFnom.getScene().getWindow();

            // Remplacez le contenu de la scène actuelle avec la scène de connexion
            currentStage.getScene().setRoot(root);

        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de connexion.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
