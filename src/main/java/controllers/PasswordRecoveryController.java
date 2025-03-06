package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import tools.DatabaseConnection;

public class PasswordRecoveryController {

    @FXML
    private TextField TFphone;

    @FXML
    private void sendCodeByPhone(ActionEvent event) {
        String phoneNumber = TFphone.getText().trim();

        // Vérifier si le champ téléphone est vide
        if (phoneNumber.isEmpty()) {
            showAlert("Error", "Please enter a valid phone number.");
            return;
        }

        // Vérifier si le numéro de téléphone est enregistré
        if (!isPhoneNumberRegistered(phoneNumber)) {
            showAlert("Error", "This phone number is not registered.");
            return;
        }

        // Générer un code de vérification simple
        String code = generateCode();

        // Afficher le code dans une alerte (vous pouvez l'envoyer par un autre moyen si nécessaire)
        showAlert("Success", "A code has been generated: " + code);
    }

    // Méthode pour vérifier si le numéro de téléphone est enregistré
    private boolean isPhoneNumberRegistered(String phoneNumber) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE telephone = ?";
        try (Connection conn = DatabaseConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            showAlert("Database Error", "Error checking phone number in database.");
            e.printStackTrace();
            return false;
        }
    }

    // Générer un code de récupération simple (par exemple, un nombre aléatoire)
    private String generateCode() {
        int code = (int) (Math.random() * 900000) + 100000; // Générer un code à 6 chiffres
        return String.valueOf(code);
    }

    @FXML
    private void backToSignInAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
        Scene signInScene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(signInScene);
        stage.show();
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
