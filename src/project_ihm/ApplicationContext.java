package project_ihm;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private static String etudiantUsername;
    private static String etudiantNumeroEtudiant;

    // Additional fields as needed
    private static Map<String, Object> etudiantContext = new HashMap<>();

    public static String getEtudiantUsername() {
        return etudiantUsername;
    }

    public static void setEtudiantUsername(String username) {
        etudiantUsername = username;
    }

    public static String getEtudiantNumeroEtudiant() {
        return etudiantNumeroEtudiant;
    }

    public static void setEtudiantNumeroEtudiant(String numeroEtudiant) {
        etudiantNumeroEtudiant = numeroEtudiant;
    }

    public static Map<String, Object> getEtudiantContext() {
        return etudiantContext;
    }

    public static void setEtudiantContext(Map<String, Object> etudiant) {
        setEtudiantUsername(etudiant.get("nom") + "." + etudiant.get("prenom"));
        setEtudiantNumeroEtudiant(etudiant.get("numeroEtudiant").toString());

        // Add more fields as needed
        etudiantContext.put("numeroEtudiant", etudiant.get("numeroEtudiant").toString());
        etudiantContext.put("nom", etudiant.get("nom").toString());
        etudiantContext.put("prenom", etudiant.get("prenom").toString());
    }
}
