package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserController {

    @FXML
    private TextField TFnom;

    @FXML
    private TextField TFprenom;

    @FXML
    private TextField TFemail;

    @FXML
    private TextField TFtelephone;  // Ajout du champ téléphone

    @FXML
    private CheckBox chkFreelance;

    @FXML
    private CheckBox chkFormateur;

    @FXML
    private CheckBox chkEmployeur;

    @FXML
    private CheckBox chkAdmin;

    private Utilisateur utilisateur;
    private final UtilisateurService serviceUtilisateur = new UtilisateurService();

    /**
     * 📌 Remplit le formulaire avec les données de l'utilisateur sélectionné pour modification.
     */
    public void setUtilisateur(Utilisateur user) {
        this.utilisateur = user;
        TFnom.setText(user.getNom());
        TFprenom.setText(user.getPrenom());
        TFemail.setText(user.getEmail());
        TFtelephone.setText(user.getTelephone());  // Remplir le champ téléphone avec la valeur existante

        // Sélectionner la case à cocher correspondant au type de l'utilisateur
        switch (user.getTypeUtilisateur()) {
            case "Freelance":
                chkFreelance.setSelected(true);
                break;
            case "Formateur":
                chkFormateur.setSelected(true);
                break;
            case "Employeur":
                chkEmployeur.setSelected(true);
                break;
            case "Admin":
                chkAdmin.setSelected(true);
                break;
        }
    }

    /**
     * 📌 Sauvegarde les modifications apportées à l'utilisateur.
     */
    @FXML
    private void updateUserAction(ActionEvent event) {
        String nom = TFnom.getText().trim();
        String prenom = TFprenom.getText().trim();
        String email = TFemail.getText().trim();
        String telephone = TFtelephone.getText().trim();  // Récupérer la valeur du téléphone
        String typeUtilisateur = getSelectedUserType();

        if (typeUtilisateur == null) {
            showAlert("Erreur", "Veuillez sélectionner un type d'utilisateur.");
            return;
        }

        // Vérification des changements
        boolean isUnchanged =
                nom.equals(utilisateur.getNom()) &&
                        prenom.equals(utilisateur.getPrenom()) &&
                        email.equals(utilisateur.getEmail()) &&
                        telephone.equals(utilisateur.getTelephone()) &&  // Vérifier le téléphone
                        typeUtilisateur.equals(utilisateur.getTypeUtilisateur());

        if (isUnchanged) {
            showAlert("Aucune modification", "Aucune modification détectée !");
            closeWindow();
            return;
        }

        // Validation des données
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Erreur", "Email invalide !");
            return;
        }

        if (telephone != null && !telephone.matches("\\d{8}")) {  // Validation du téléphone
            showAlert("Erreur", "Le numéro de téléphone doit contenir 8 chiffres.");
            return;
        }

        // Mettre à jour les informations de l'utilisateur
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setTelephone(telephone);  // Mettre à jour le téléphone
        utilisateur.setTypeUtilisateur(typeUtilisateur);

        // Mettre à jour l'utilisateur dans le service
        if (serviceUtilisateur.mettreAJourUtilisateur(utilisateur)) {
            showAlert("Succès", "Modification réussie !");
            DashboardController dashboard = DashboardController.getInstance();
            if (dashboard != null) {
                dashboard.refreshUserList();
            }
            closeWindow();
        } else {
            showAlert("Erreur", "Échec de la mise à jour.");
        }
    }

    /**
     * 📌 Récupère le type d'utilisateur sélectionné.
     */
    private String getSelectedUserType() {
        List<String> selectedTypes = new ArrayList<>();
        if (chkFreelance.isSelected()) selectedTypes.add("Freelance");
        if (chkFormateur.isSelected()) selectedTypes.add("Formateur");
        if (chkEmployeur.isSelected()) selectedTypes.add("Employeur");
        if (chkAdmin.isSelected()) selectedTypes.add("Admin");

        return selectedTypes.isEmpty() ? null : String.join(", ", selectedTypes);
    }

    /**
     * 📌 Ferme la fenêtre actuelle.
     */
    private void closeWindow() {
        Stage stage = (Stage) TFnom.getScene().getWindow();
        stage.close();
    }

    /**
     * 📌 Affiche une boîte de dialogue avec un message donné.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
