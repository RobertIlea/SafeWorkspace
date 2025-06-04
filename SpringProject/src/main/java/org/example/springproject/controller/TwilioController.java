/**
 * TwilioController.java
 * This controller handles HTTP requests related to Twilio phone call and SMS functionalities.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.controller;

import org.example.springproject.exception.CreationException;
import org.example.springproject.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TwilioController handles HTTP requests for making phone calls and sending SMS messages using Twilio.
 * It is marked with @RestController to indicate that it is a REST-ful controller.
 * It is mapped to the "/api/twilio" URL path.
 */
@RestController
@RequestMapping("/api/twilio")
public class TwilioController {

    /**
     * The TwilioService is injected to handle business logic related to Twilio phone calls and SMS.
     */
    @Autowired
    private TwilioService twilioService;

    /**
     * This method handles POST requests to make a phone call.
     * @param number the phone number to call
     * @return ResponseEntity indicating the success of the operation
     * @throws CreationException if the phone number is null or empty
     */
    @PostMapping("/call")
    public ResponseEntity<String> callNumber(@RequestParam String number) throws CreationException {
        twilioService.makeCall(number);

        if (number == null || number.isEmpty()) {
            throw new CreationException("Phone number cannot be null or empty!");
        }

        return new ResponseEntity<>("Phone number was called successfully!",HttpStatus.OK);
    }

    /**
     * This method handles POST requests to send an SMS message.
     * @param number the phone number to send the message to
     * @param message the content of the message
     * @return ResponseEntity indicating the success of the operation
     * @throws CreationException if the phone number or message is null or empty
     */
    @PostMapping("/message")
    public ResponseEntity<String> sendMessage(@RequestParam String number, @RequestParam String message) throws CreationException {
            twilioService.sendSms(number, message);

            if (number == null || number.isEmpty()) {
                throw new CreationException("Phone number cannot be null or empty!");
            }

            if (message == null || message.isEmpty()) {
                throw new CreationException("Message cannot be null or empty!");
            }

            return new ResponseEntity<>("Message was sent successfully!",HttpStatus.OK);
    }

}
