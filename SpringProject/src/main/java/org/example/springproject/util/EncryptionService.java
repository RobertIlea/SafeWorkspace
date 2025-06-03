/**
 * EncryptionService.java
 * This class provides methods to encrypt and decrypt phone numbers using AES encryption.
 * It uses a secret key defined in the application properties.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * EncryptionService class provides methods to encrypt and decrypt phone numbers.
 * It uses AES encryption with a key defined in the application properties.
 */
@Component
public class EncryptionService {

    /**
     * The secret key used for AES encryption.
     * It is injected from the application properties file.
     */
    @Value("${encryption.key}")
    private String key;

    /**
     * Returns a SecretKeySpec object initialized with the encryption key.
     * This key is used for AES encryption and decryption.
     * @return SecretKeySpec object initialized with the encryption key.
     */
    private SecretKeySpec getKey(){
        return new SecretKeySpec(key.getBytes(), "AES");
    }

    /**
     * Encrypts the given plain text using AES encryption.
     * @param plainText The text to be encrypted.
     * @return The encrypted text as a Base64 encoded string.
     * @throws RuntimeException If an error occurs during encryption.
     */
    public String encrypt(String plainText)throws RuntimeException {
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
        }catch(Exception e){
            System.err.println("ERROR while encrypting: " + plainText);
            throw new RuntimeException("Error while encrypting the phone number: " + e.getMessage());
        }

    }

    /**
     * Decrypts the given encrypted text using AES decryption.
     * @param encryptedText The text to be decrypted, which is expected to be Base64 encoded.
     * @return The decrypted plain text.
     * @throws RuntimeException If an error occurs during decryption.
     */
    public String decrypt(String encryptedText) throws RuntimeException {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
        } catch (Exception e) {
            System.err.println("ERROR while decrypting: " + encryptedText);
            throw new RuntimeException("Error while decrypting the phone number: " + e.getMessage());
        }
    }

}
