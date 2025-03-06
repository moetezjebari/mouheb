package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class UserController {

    @FXML
    private Label labelNom;

    @FXML
    private Label labelPrenom;

    @FXML
    private Label labelEmail;

    @FXML
    private Label labelType;

    @FXML
    private Label labelDateInscription;

    @FXML
    private Label labelTelephone;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnModifier;

    private Utilisateur utilisateur;
    private final UtilisateurService serviceUtilisateur = new UtilisateurService();

    /**
     * 📌 Définit les données utilisateur à afficher dans la carte.
     */
    public void setData(Utilisateur user) {
        this.utilisateur = user;
        labelNom.setText(user.getNom());
        labelPrenom.setText(user.getPrenom());
        labelEmail.setText(user.getEmail());
        labelType.setText(user.getTypeUtilisateur());

        // ✅ Vérification et affichage de la date d'inscription
        if (user.getDateInscription() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            labelDateInscription.setText(dateFormat.format(user.getDateInscription()));
        } else {
            labelDateInscription.setText("Date non disponible");
        }

        // ✅ Affichage du téléphone
        if (user.getTelephone() != null && !user.getTelephone().isEmpty()) {
            labelTelephone.setText(user.getTelephone());
        } else {
            labelTelephone.setText("Téléphone non disponible");
        }
    }

    /**
     * 📌 Ajoute la méthode pour modifier uniquement la date d'inscription.
     */
    public void setDateInscription(String date) {
        labelDateInscription.setText(date);
    }

    // ✅ Correction de la méthode setTelephone (sans validation)
    public void setTelephone(String telephone) {
        utilisateur.setTelephone(telephone);  // Mise à jour du téléphone de l'utilisateur dans l'objet Utilisateur
        labelTelephone.setText(telephone);  // Met à jour l'affichage du téléphone dans le label
    }

    /**
     * 📌 Supprime un utilisateur après confirmation.
     */
    @FXML
    private void supprimerUtilisateur() {
        if (showConfirmation("Confirmation", "Voulez-vous vraiment supprimer cet utilisateur ?")) {
            if (serviceUtilisateur.supprimerUtilisateur(utilisateur.getId())) {
                DashboardController dashboard = DashboardController.getInstance();
                if (dashboard != null) {
                    dashboard.refreshUserList();
                }
                showAlert("Succès", "Utilisateur supprimé avec succès !");
            } else {
                showAlert("Erreur", "Erreur lors de la suppression.");
            }
        }
    }

    /**
     * 📌 Ouvre la fenêtre de modification d’un utilisateur après confirmation.
     */
    @FXML
    private void modifierUtilisateur() {
        if (showConfirmation("Confirmation", "Voulez-vous vraiment modifier cet utilisateur ?")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateUser.fxml"));
                Parent root = loader.load();

                UpdateUserController controller = loader.getController();
                controller.setUtilisateur(utilisateur);

                Stage stage = new Stage();
                stage.setTitle("Modifier Utilisateur");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir la page de modification.");
                e.printStackTrace();
            }
        }
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

    /**
     * 📌 Affiche une boîte de confirmation avec boutons "OK" et "Annuler".
     */
    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
