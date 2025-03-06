package services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

import javax.mail.Message.RecipientType;  // Importation ajoutée

public class EmailService {
    private static final String USER = "mouheb.sayadi@esprit.tn";  // Votre adresse email
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static void sendWelcomeEmail(String destinataire) {
        String sujet = "Bienvenue chez nous !";
        String contenu = "Bienvenue ! Nous sommes ravis de vous avoir parmi nous. Si vous avez des questions, n'hésitez pas à nous contacter.";

        try {
            // Initialiser le transport sécurisé
            Credential credential = GmailOAuth.getCredentials(GoogleNetHttpTransport.newTrustedTransport());

            // Créer une instance du service Gmail
            Gmail service = new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName("Mon Application Gmail OAuth2")
                    .build();

            // Configuration de la session email
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USER, "votre_mot_de_passe");
                }
            });

            // Création de l'email à envoyer
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(USER));
            email.addRecipient(RecipientType.TO, new InternetAddress(destinataire));  // Utilisation de RecipientType
            email.setSubject(sujet);
            email.setText(contenu);

            // Convertir l'email au format Gmail API
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.getUrlEncoder().encodeToString(rawMessageBytes);

            Message message = new Message();
            message.setRaw(encodedEmail);

            // Envoyer l'email via Gmail API
            service.users().messages().send("me", message).execute();
            System.out.println("✅ Email envoyé avec succès !");
        } catch (GeneralSecurityException | MessagingException | IOException e) {
            // Gérer les exceptions
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();  // Afficher la trace de l'exception
        }
    }
}