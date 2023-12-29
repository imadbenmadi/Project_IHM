package project_ihm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
