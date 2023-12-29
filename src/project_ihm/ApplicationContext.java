package project_ihm;

import java.util.Map;

public class ApplicationContext {
    private static String etudiantUsername;
    // Add more fields as needed

    public static String getEtudiantUsername() {
        return etudiantUsername;
    }

    public static void setEtudiantUsername(String username) {
        etudiantUsername = username;
    }

    // Add more getter and setter methods for additional fields
    public static void setEtudiantContext(Map<String, Object> etudiant) {
        setEtudiantUsername(etudiant.get("numeroEtudiant").toString());
        // Add more fields as needed
    }
}
