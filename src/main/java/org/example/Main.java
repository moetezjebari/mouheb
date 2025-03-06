package org.example;

import models.Utilisateur;
import services.UtilisateurService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 🔥 Classe principale du projet JavaFX
 * Cette classe lance l'application JavaFX et affiche la première interface utilisateur.
 */
public class Main extends Application {

    /**
     * 📌 Méthode `start(Stage stage)`
     * Cette méthode est exécutée lorsque l'application JavaFX démarre.
     * Elle charge et affiche la scène principale (Page de connexion `SignIn.fxml`).
     *
     * @param stage Fenêtre principale de l'application.
     * @throws Exception Si un fichier FXML est introuvable ou en cas d'erreur d'affichage.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // 🔹 Chargement de l'interface `SignIn.fxml`
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
        Parent root = loader.load(); // Chargement du fichier FXML

        // 🔹 Création d'une scène contenant l'interface utilisateur
        Scene scene = new Scene(root);

        // 🔹 Configuration de la fenêtre principale (stage)
        stage.setScene(scene);
        stage.setTitle("Gestion Utilisateur"); // Titre de la fenêtre
        stage.show(); // Affichage de la fenêtre
    }

    /**
     * 📌 Méthode `main(String[] args)`
     * Cette méthode est le point d'entrée du programme.
     * Elle lance l'application JavaFX.
     *
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        launch(); // 🔥 Démarre l'application JavaFX
    }
}
