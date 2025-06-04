/**
 * MailConfig.java
 * Configuration class for setting up email sending capabilities.
 * This class uses Spring's JavaMailSender to send emails.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * MailConfig class is responsible for configuring the JavaMailSender.
 * It is marked as a Spring configuration class with the @Configuration annotation so that the Spring container can recognize it.
 */
@Configuration
public class MailConfig {

    /**
     * The host of the mail server.
     * This is the SMTP server address.
     * @Value annotation is used to inject the value from application properties.
     */
    @Value("${spring.mail.host}")
    private String host;

    /**
     * The port of the mail server.
     * This is typically 587 for TLS.
     * @Value annotation is used to inject the value from application properties.
     */
    @Value("${spring.mail.port}")
    private int port;

    /**
     * The username for the mail server.
     * This is the email address used to send emails.
     * @Value annotation is used to inject the value from application properties.
     */
    @Value("${spring.mail.username}")
    private String username;

    /**
     * The password for the mail server.
     * This is the password for the email account used to send emails.
     * @Value annotation is used to inject the value from application properties.
     */
    @Value("${spring.mail.password}")
    private String password;

    /**
     * Creates a JavaMailSender bean.
     * This method configures the JavaMailSender with the SMTP server details.
     * It sets the host, port, username, and password for the mail server.
     * It is annotated with @Bean so that it can be managed by the Spring container.
     * @return JavaMailSender instance configured with the mail server details.
     */
    @Bean
    JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    /**
     * Creates a SimpleMailMessage bean.
     * This method provides a template for sending simple text emails.
     * It sets the default "from" address and subject for the emails.
     * It is annotated with @Bean so that it can be managed by the Spring container.
     * @return SimpleMailMessage instance configured with default values.
     */
    @Bean
    public SimpleMailMessage templateMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("room.monitoring.system03@gmail.com");
        message.setSubject("Sensor Alert Notification");
        return message;
    }
}
