package services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class GmailOAuth {
    private static final String APPLICATION_NAME = "Mon Application Gmail OAuth2";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/gmail.send");
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json"; // Placez le fichier ici

    // Méthode pour obtenir les credentials OAuth2
    public static Credential getCredentials(HttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GmailOAuth.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Fichier credentials.json introuvable !");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    // Méthode pour envoyer un e-mail via Gmail
    public static void sendEmail(String to, String subject, String bodyText) throws Exception {
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        MimeMessage email = createEmail(to, subject, bodyText);
        sendMessage(service, "me", email);
    }

    // Créer un e-mail à partir des données de l'utilisateur
    public static MimeMessage createEmail(String to, String subject, String bodyText) throws MessagingException {
        MimeMessage email = new MimeMessage(Session.getDefaultInstance(System.getProperties()));
        // Remplacez par votre adresse email
        email.setFrom(new InternetAddress("mouheb.sayadi@esprit.tn"));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    // Envoyer un message via Gmail API
    public static void sendMessage(Gmail service, String userId, MimeMessage email) throws IOException, MessagingException {
        Message message = createMessageWithEmail(email);
        service.users().messages().send(userId, message).execute();
    }

    // Convertir l'e-mail en un message Gmail API
    public static Message createMessageWithEmail(MimeMessage email) throws IOException, MessagingException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        email.writeTo(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Message message = new Message();
        message.setRaw(Base64.getEncoder().encodeToString(bytes));
        return message;
    }
}
