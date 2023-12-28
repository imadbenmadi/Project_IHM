package project_ihm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Project_IHM extends Application {

    private static Project_IHM instance; // Singleton instance

    public static Project_IHM getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this; // Set the singleton instance

        try {
            // Load the Login FXML file and show the login window
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loginLoader.load();
            Scene loginScene = new Scene(loginRoot);
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Login");
            primaryStage.show();

            // Get the controller for the Login window
            LoginController loginController = loginLoader.getController();

            // Set a reference to the main application in the LoginController
            loginController.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showLogin() {
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loginLoader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loginRoot));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to show the Dashboard window
    public void showDashboard() {
        try {
            FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent dashboardRoot = dashboardLoader.load();
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(new Scene(dashboardRoot));
            dashboardStage.setTitle("Dashboard");
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to show the Etudiant window
    public void showEtudiant() {
        try {
            FXMLLoader etudiantLoader = new FXMLLoader(getClass().getResource("Etudiant.fxml"));
            Parent etudiantRoot = etudiantLoader.load();
            Stage etudiantStage = new Stage();
            etudiantStage.setScene(new Scene(etudiantRoot));
            etudiantStage.setTitle("Etudiant");
            etudiantStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
