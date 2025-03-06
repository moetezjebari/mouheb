package tools;

import models.Utilisateur;

/**
 * 🔥 Gère la session de l'utilisateur connecté
 */
public class SessionManager {
    private static SessionManager instance;
    private Utilisateur loggedInUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Utilisateur getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(Utilisateur user) {
        this.loggedInUser = user;
    }

    public void clearSession() {
        loggedInUser = null;
    }
}
