package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import tools.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatistiqueController {

    @FXML
    private PieChart piechart;

    private final ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
    private final Connection conn = DatabaseConnection.getInstance().getCnx();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    /**
     * 📌 Charge les statistiques des utilisateurs en fonction de leur type (Admin, Normal User, etc.).
     */
    private void loadStatistics() {
        try {
            String query = "SELECT type_utilisateur, COUNT(*) as count FROM utilisateur GROUP BY type_utilisateur;";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                String typeUtilisateur = rs.getString("type_utilisateur");
                int count = rs.getInt("count");

                data.add(new PieChart.Data(typeUtilisateur + " (" + count + ")", count));
            }

            piechart.setData(data);
        } catch (SQLException ex) {
            System.out.println("Erreur lors du chargement des statistiques: " + ex.getMessage());
        }
    }
}
