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

public class MailService {
    private static final String USER = "mouheb.sayadi@esprit.tn"; // Utilisez votre adresse Gmail ici
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static void envoyerEmail(String destinataire, String sujet, String contenu) throws GeneralSecurityException, IOException, MessagingException {
        try {
            // Initialiser le transport sécurisé avec les credentials OAuth
            Credential credential = GmailOAuth.getCredentials(GoogleNetHttpTransport.newTrustedTransport());

            // Créer une instance du service Gmail
            Gmail service = new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName("Mon Application Gmail OAuth2")
                    .build();

            // Configuration de la session email (si vous utilisez SMTP, mais cela ne semble pas nécessaire ici)
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USER, "rouaderouiche123"); // Utiliser des variables d'environnement pour sécuriser le mot de passe
                }
            });

            // Créer l'email à envoyer
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(USER));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(destinataire));
            email.setSubject(sujet);
            email.setText(contenu);

            // Convertir l'email au format Gmail API (encodage base64)
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.getUrlEncoder().encodeToString(rawMessageBytes);

            Message message = new Message();
            message.setRaw(encodedEmail);

            // Envoyer l'email via Gmail API
            service.users().messages().send("me", message).execute();
            System.out.println("✅ Email envoyé avec succès !");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
            throw e;
        }
    }
}
