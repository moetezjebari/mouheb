package models;
import java.sql.Timestamp;

/**
 * 🔥 Classe représentant un utilisateur.
 */
public class Utilisateur {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String typeUtilisateur;
    private String telephone;  // Ajout du champ pour le téléphone
    private Timestamp dateInscription; // ✅ Ajout de la date d'inscription

    /**
     * ✅ Constructeur avec ID et Date d'inscription (Utilisé pour récupérer un utilisateur depuis la base)
     */
    public Utilisateur(int id, String nom, String prenom, String email, String typeUtilisateur, String telephone, Timestamp dateInscription) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.typeUtilisateur = typeUtilisateur;
        this.telephone = telephone;
        this.dateInscription = dateInscription;
    }

    /**
     * ✅ Constructeur AVEC ID mais sans Timestamp (Pour éviter l'erreur)
     */
    public Utilisateur(int id, String nom, String prenom, String email, String typeUtilisateur, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.typeUtilisateur = typeUtilisateur;
        this.telephone = telephone;
    }

    /**
     * ✅ Constructeur sans ID (Utilisé lors de l'inscription)
     */
    public Utilisateur(String nom, String prenom, String email, String motDePasse, String telephone, String typeUtilisateur) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;  // Initialisation du téléphone
        this.typeUtilisateur = typeUtilisateur;
        this.dateInscription = new Timestamp(System.currentTimeMillis()); // ✅ Génère automatiquement la date d'inscription
    }

    // ✅ Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTypeUtilisateur() {
        return typeUtilisateur;
    }

    public void setTypeUtilisateur(String typeUtilisateur) {
        this.typeUtilisateur = typeUtilisateur;
    }

    public String getTelephone() {
        return telephone;  // Getter pour le téléphone
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;  // Setter pour le téléphone
    }

    public Timestamp getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Timestamp dateInscription) {
        this.dateInscription = dateInscription;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +  // Ajout du téléphone
                ", typeUtilisateur='" + typeUtilisateur + '\'' +
                ", dateInscription=" + dateInscription +
                '}';
    }
}
