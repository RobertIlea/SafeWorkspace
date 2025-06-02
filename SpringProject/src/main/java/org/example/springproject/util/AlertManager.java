/**
 * AlertManager.java
 * This class is responsible for managing alert notifications in the system.
 * It sends email notifications to users when an alert is triggered by a sensor.
 * It uses Spring's MailSender to send emails and SimpleMailMessage as a template for the email content.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.util;

import org.example.springproject.entity.Alert;
import org.example.springproject.entity.Sensor;
import org.example.springproject.entity.User;
import org.example.springproject.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

/**
 * AlertManager is a component that handles sending alert emails to users.
 */
@Component
public class AlertManager {
    /**
     * The MailSender used to send emails.
     */
    private MailSender mailSender;

    /**
     * The template message used for alert emails.
     */
    private SimpleMailMessage templateMessage;

    /**
     * The RoomService used to retrieve room information.
     */
    private final RoomService roomService;

    /**
     * Constructor for AlertManager.
     * @param mailSender the MailSender to use for sending emails
     * @param templateMessage the template message for alert emails
     * @param roomService the RoomService to retrieve room information
     */
    @Autowired
    public AlertManager(MailSender mailSender, SimpleMailMessage templateMessage, RoomService roomService) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
        this.roomService = roomService;
    }

    /**
     * Sets the MailSender to be used by this AlertManager.
     * @param mailSender the MailSender to set
     */
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sets the template message to be used for alert emails.
     * @param templateMessage the SimpleMailMessage template to set
     */
    public void setSimpleMailMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    /**
     * Sends an alert email to the user when a sensor detects an alert.
     * @param user the User to whom the alert email will be sent
     * @param alert the Alert that triggered the email
     * @param sensor the Sensor that detected the alert
     */
    public void sendEmail(User user, Alert alert, Sensor sensor) {
        System.out.println("Sending alert email to: " + user.getEmail());

        // Creating a thread-safe "copy" of the template message and then customize it
        SimpleMailMessage message = new SimpleMailMessage(this.templateMessage);
        message.setTo(user.getEmail());
        message.setText(
                "Dear " + user.getName() + ",\n\n" +
                        "The sensor **" + sensor.getSensorType() + "** in room **" + roomService.getRoomById(alert.getRoomId()).getName() + "**\n" +
                        "has detected an alert:\n\n" +
                        alert.getMessage() + "\n\n" +
                        "Please check the system as soon as possible.\n\n" +
                        "Best regards,\nRoom Monitoring System!"
        );

        try {
            this.mailSender.send(message);
            System.out.println("@ Alert email sent! @");
        }
        catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
