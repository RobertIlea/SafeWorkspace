/**
 * TwilioServiceImpl.java
 * This file is part of the Spring Project.
 * It is used to implement the TwilioService interface for handling Twilio operations.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service.implementation;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.example.springproject.service.TwilioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * TwilioServiceImpl is a service class that implements the TwilioService interface.
 * It provides methods for making calls and sending SMS using the Twilio API.
 */
@Service
public class TwilioServiceImpl implements TwilioService {

    /**
     * The phone number associated with the Twilio account.
     */
    private final String phoneNumber;

    /**
     * Constructor for TwilioServiceImpl.
     * Initializes the Twilio client with the account SID and auth token.
     *
     * @param accountSid The Twilio account SID, injected from application properties.
     * @param accountToken The Twilio auth token, injected from application properties.
     * @param phoneNumber The Twilio phone number, injected from application properties.
     */
    public TwilioServiceImpl(@Value("${twilio.account-sid}") String accountSid, @Value("${twilio.auth-token}") String accountToken, @Value("${twilio.phone-number}") String phoneNumber) {
        this.phoneNumber = phoneNumber;
        Twilio.init(accountSid, accountToken);
    }

    /**
     * Makes a call to the specified phone number using the Twilio API.
     * @param toNumber The phone number to call.
     */
    @Override
    public void makeCall(String toNumber) {
        try{
            Call call = Call.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(phoneNumber),
                    URI.create("http://demo.twilio.com/docs/voice.xml")).create();
        }catch (Exception e){
            System.err.println("Failed to make call: " + e.getMessage());
        }
    }

    /**
     * Sends an SMS to the specified phone number using the Twilio API.
     * @param toNumber The phone number to send the SMS to.
     * @param body The body of the SMS message.
     */
    @Override
    public void sendSms(String toNumber, String body){
        try{
            Message message = Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(phoneNumber),
                    body).create();
        }catch (Exception e){
            System.err.println("Failed to send SMS: " + e.getMessage());
        }

    }
}
