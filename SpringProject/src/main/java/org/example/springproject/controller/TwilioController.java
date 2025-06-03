package org.example.springproject.controller;

import org.example.springproject.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/twilio")
public class TwilioController {
    @Autowired
    private TwilioService twilioService;

    @PostMapping("/call")
    public ResponseEntity<String> callNumber(@RequestParam String number){
        try{
            twilioService.makeCall(number);
            return ResponseEntity.ok("Successfully call the number: " + number);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/message")
    public ResponseEntity<String> sendMessage(@RequestParam String number, @RequestParam String message){
        try{
            twilioService.sendSms(number, message);
            return ResponseEntity.ok("Successfully sent the message: " + message);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
