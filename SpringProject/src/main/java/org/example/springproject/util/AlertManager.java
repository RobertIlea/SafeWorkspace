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

@Component
public class AlertManager {
    private MailSender mailSender;
    private SimpleMailMessage templateMessage;
    private final RoomService roomService;

    @Autowired
    public AlertManager(MailSender mailSender, SimpleMailMessage templateMessage, RoomService roomService) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
        this.roomService = roomService;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void setSimpleMailMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

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
