package com.emailadmin;

import java.util.Properties;
import javax.mail.Session;
import javax.mail.Transport;

public final class SMTPVerifier {
    private SMTPVerifier() {}

    public static void verify(String host, int port, String username, String password)
            throws Exception {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", String.valueOf(port));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(properties);
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect(host, port, username, password);
        }
    }
}
