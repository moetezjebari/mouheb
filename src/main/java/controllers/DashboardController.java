package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurService;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardController implements Initializable {

    @FXML
    private GridPane grid;
    @FXML
    private ComboBox<String> comboTriDate;
    @FXML
    private TextField searchField;

    private final UtilisateurService serviceUtilisateur = new UtilisateurService();
    private List<Utilisateur> userList = new ArrayList<>();
    private List<Utilisateur> filteredList = new ArrayList<>();
    private boolean triAncien = true;
    private static DashboardController instance;

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboTriDate.getItems().setAll("Sélectionner le tri", "Plus ancien → Plus récent", "Plus récent → Plus ancien");
        comboTriDate.setValue("Sélectionner le tri");
        comboTriDate.setOnAction(this::trierParDate);
        refreshUserList();
    }

    public void refreshUserList() {
        userList = serviceUtilisateur.getAllUtilisateurs();
        filtrerEtAfficher();  // Afficher tous les utilisateurs après le refresh
    }

    private void appliquerTri() {
        if (triAncien) {
            userList.sort(Comparator.comparing(Utilisateur::getDateInscription));
        } else {
            userList.sort(Comparator.comparing(Utilisateur::getDateInscription).reversed());
        }
    }

    private void afficherUtilisateurs(List<Utilisateur> utilisateurs) {
        grid.getChildren().clear();
        for (Utilisateur user : utilisateurs) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserComponent.fxml"));
                Parent userPane = loader.load();
                UserController controller = loader.getController();
                controller.setData(user);

                if (user.getDateInscription() != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    controller.setDateInscription(dateFormat.format(user.getDateInscription()));
                } else {
                    controller.setDateInscription("N/A");
                }

                // Afficher le téléphone
                controller.setTelephone(user.getTelephone());  // ✅ Ajout du téléphone ici

                grid.add(userPane, 0, grid.getChildren().size());
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de charger un utilisateur !");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void trierParDate(ActionEvent event) {
        String choix = comboTriDate.getValue();
        if ("Plus ancien → Plus récent".equals(choix)) {
            triAncien = true;
        } else if ("Plus récent → Plus ancien".equals(choix)) {
            triAncien = false;
        }
        appliquerTri();
        filtrerEtAfficher();  // Appliquer le tri et afficher tous les utilisateurs
    }

    @FXML
    private void rechercherParNom() {
        filtrerEtAfficher();
    }

    private void filtrerEtAfficher() {
        String searchText = searchField.getText().toLowerCase().trim();
        filteredList.clear();

        for (Utilisateur user : userList) {
            if (searchText.isEmpty() || user.getNom().toLowerCase().contains(searchText)) {
                filteredList.add(user);
            }
        }

        afficherUtilisateurs(filteredList);  // Afficher tous les utilisateurs filtrés
    }

    @FXML
    private void ouvrirAjouterUtilisateur(ActionEvent event) {
        openWindow("/AjouterUser.fxml", "Ajouter un Utilisateur");
    }

    @FXML
    private void afficherStatistiques(ActionEvent event) {
        openWindow("/Statistique.fxml", "Statistiques des Utilisateurs");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Confirmation de déconnexion");
        alert.setContentText("Vous êtes sur le point de vous déconnecter.\nConfirmez-vous cette action ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) grid.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de retourner à la page de connexion.");
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
        }
    }
}
