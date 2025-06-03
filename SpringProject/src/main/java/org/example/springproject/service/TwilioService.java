/**
 * TwilioService.java
 * This interface defines the contract for Twilio-related operations in the application.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service;

/**
 * TwilioService provides methods to make calls and send SMS messages using Twilio's API.
 */
public interface TwilioService {

    /**
     * Makes a call to the specified phone number.
     * @param toNumber the phone number to call
     */
    void makeCall(String toNumber);

    /**
     * Sends an SMS message to the specified phone number.
     * @param toNumber the phone number to which the SMS will be sent
     * @param body the content of the SMS message
     */
    void sendSms(String toNumber, String body);
}
