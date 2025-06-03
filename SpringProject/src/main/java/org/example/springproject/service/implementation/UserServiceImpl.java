/**
 * UserServiceImpl.java
 * This file is part of the Spring Project.
 * It is used to implement the UserService interface for handling user-related operations.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;
import org.example.springproject.service.UserService;
import org.example.springproject.util.EncryptionService;
import org.example.springproject.util.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * UserServiceImpl is a service class that implements the UserService interface.
 * It provides methods for managing users, including adding, deleting, updating, and retrieving user information.
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * Firestore instance for interacting with the Firestore database.
     */
    private final Firestore firestore;

    /**
     * PasswordEncoder instance for encoding passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * EncryptionService instance for encrypting and decrypting user's phone number.
     */
    private final EncryptionService encryptionService;

    /**
     * The name of the Firestore collection for rooms.
     */
    private static final String ROOM_COLLECTION = "rooms";

    /**
     * The name of the Firestore collection for users.
     */
    private static final String USER_COLLECTION = "users";

    /**
     * Constructor for UserServiceImpl.
     * @param firestore The Firestore instance for database operations.
     * @param encryptionService The EncryptionService instance for phone number encryption.
     */
    public UserServiceImpl(Firestore firestore,EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.firestore = firestore;
    }

    /**
     * Validates the email format using a regular expression.
     * @param email The email to validate.
     * @return true if the email is valid, false otherwise.
     */
    private boolean isEmailValid(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * Verifies the user details before adding or updating a user.
     * Throws an exception if any validation fails.
     * @param user The user to verify.
     */
    public void userVerification(User user) {
        if(!isEmailValid(user.getEmail())){
            throw new RuntimeException("Invalid email format!");
        }
        if(user.getName().length() < 5 || user.getName().length() > 32){
            throw new RuntimeException("Name length must be between 5 and 32");
        }
        if(user.getPassword().isEmpty() || user.getPassword().length() < 8 ){
            throw new RuntimeException("Length of the password must be greater than 8 characters");
        }
    }

    /**
     * Adds a new user to the database.
     * @param user The user to add.
     * @return UserDTO containing the user's details.
     * @throws RuntimeException if an error occurs while adding the user.
     */
    @Override
    public UserDTO addUser(User user) throws RuntimeException {
        try{
            userVerification(user);
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            Query emailQuery = firestore.collection(USER_COLLECTION).whereEqualTo("email",user.getEmail());
            QuerySnapshot emailQuerySnapshot = emailQuery.get().get();

            if(!emailQuerySnapshot.isEmpty()){
                throw new RuntimeException("User with email: " + user.getEmail() + " already exist!");
            }

            DocumentReference userRef = firestore.collection(USER_COLLECTION).document();
            userRef.set(user).get();

            return new UserDTO(userRef.getId(),user.getName(),user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Error while adding a new user: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user by their ID.
     * @param id The ID of the user to delete.
     * @return UserDTO containing the deleted user's details.
     * @throws RuntimeException if an error occurs while deleting the user or if the user does not exist.
     */
    @Override
    public UserDTO deleteUserById(String id) throws RuntimeException{
        try{
            DocumentReference userRef = firestore.collection(USER_COLLECTION).document(id);
            DocumentSnapshot userSnapshot = userRef.get().get();

            if(!userSnapshot.exists()){
                throw new RuntimeException("User with id: " + id + " doesn't exist in database!");
            }

            User user = userSnapshot.toObject(User.class);

            userRef.delete().get();
            assert user != null;
            return new UserDTO(id,user.getName(),user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting the user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user by their ID.
     * @param id The ID of the user to update.
     * @param updatedUser The updated user details.
     * @return UserDTO containing the updated user's details.
     * @throws RuntimeException if an error occurs while updating the user or if the user does not exist.
     */
    @Override
    public UserDTO updateUser(String id, User updatedUser) throws RuntimeException{
        try{
            DocumentReference userRef = firestore.collection(USER_COLLECTION).document(id);
            DocumentSnapshot userSnapshot = userRef.get().get();

            if(!userSnapshot.exists()){
                throw new RuntimeException("User with id: " + id + " doesn't exist in database!");
            }

            userVerification(updatedUser);

            User currentUser = userSnapshot.toObject(User.class);
            assert currentUser != null;

            currentUser.setName(updatedUser.getName());
            currentUser.setEmail(updatedUser.getEmail());

            String encryptedPassword = passwordEncoder.encode(updatedUser.getPassword());
            currentUser.setPassword(encryptedPassword);


            userRef.set(currentUser).get();
            return new UserDTO(id, currentUser.getName(), currentUser.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Error while updating the user: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a list of all users from the database.
     * @return List of UserDTO containing details of all users.
     * @throws RuntimeException if an error occurs while fetching the users.
     */
    @Override
    public List<UserDTO> getUsers() throws RuntimeException{
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(USER_COLLECTION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            List<UserDTO> users = new ArrayList<>();

            for(QueryDocumentSnapshot document:documents){
                User user = document.toObject(User.class);
                UserDTO userDTO = UserMapper.toDTO(user, document.getId());
                users.add(userDTO);
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the users: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user to retrieve.
     * @return UserDTO containing the user's details.
     * @throws RuntimeException if an error occurs while fetching the user or if the user does not exist.
     */
    @Override
    public UserDTO getUserById(String id) throws RuntimeException{
        try{
            DocumentReference documentReference = firestore.collection(USER_COLLECTION).document(id);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();

            User user = null;
            if(!document.exists()){
                throw new RuntimeException("User with id: " + id + " doesn't exist in database!");
            }
            user = document.toObject(User.class);
            assert user != null;
            return new UserDTO(id,user.getName(),user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the user: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a user by their email.
     * @param email The email of the user to retrieve.
     * @return User containing the user's details.
     * @throws RuntimeException if an error occurs while fetching the user or if the user does not exist.
     */
    @Override
    public User getUserByEmail(String email) throws RuntimeException{
        System.out.println(email);
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(USER_COLLECTION).whereEqualTo("email", email).get();
            QuerySnapshot querySnapshot = future.get();

            if(querySnapshot.isEmpty()){
                throw new RuntimeException("User with email: " + email + " doesn't exist in database!");
            }

            QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
            System.out.println("Document id: " + document.getId());

            User user = document.toObject(User.class);

            return new User(user.getName(),user.getEmail(),user.getPassword(),user.getPhone());
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the user by email: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a user ID by their email.
     * @param email The email of the user to retrieve the ID for.
     * @return The user ID as a String.
     * @throws RuntimeException if an error occurs while fetching the user ID or if the user does not exist.
     */
    @Override
    public String getUserIdByEmail(String email) throws RuntimeException{
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(USER_COLLECTION).whereEqualTo("email", email).get();
            QuerySnapshot querySnapshot = future.get();

            if(querySnapshot.isEmpty()){
                throw new RuntimeException("User with email: " + email + " doesn't exist in database!");
            }

            QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
            return document.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the user id by email: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a user by their room ID.
     * @param roomId The ID of the room to retrieve the user for.
     * @return UserDTO containing the user's details.
     * @throws RuntimeException if an error occurs while fetching the user or if the room does not exist.
     */
    @Override
    public UserDTO getUserByRoomId(String roomId) throws RuntimeException{
        try{
            DocumentSnapshot roomSnapshot = firestore.collection(ROOM_COLLECTION).document(roomId).get().get();

            if(!roomSnapshot.exists()){
                throw new RuntimeException("Room with id: " + roomId + " doesn't exist in database!");
            }

            String userId = roomSnapshot.getString("userId");

            if(userId == null){
                throw new RuntimeException("Error while getting user id from room id: " + roomId);
            }

            DocumentSnapshot userSnapshot = firestore.collection(USER_COLLECTION).document(userId).get().get();

            if(!userSnapshot.exists()){
                throw new RuntimeException("User with id: " + userId + " doesn't exist in database!");
            }

            User user = userSnapshot.toObject(User.class);

            if(user == null){
                throw new RuntimeException("Error while getting user from room id: " + roomId);
            }

            return new UserDTO(userId,user.getName(),user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the user by room id: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the user's phone number.
     * @param userId The ID of the user whose phone number is to be updated.
     * @param phone The new phone number to set for the user.
     * @throws RuntimeException if an error occurs while updating the phone number or if the user does not exist.
     */
    @Override
    public void updateUserPhone(String userId, String phone) throws RuntimeException{
        try{
            DocumentReference documentReference = firestore.collection(USER_COLLECTION).document(userId);

            if(!documentReference.get().get().exists()){
                throw new RuntimeException("User with id: " + userId + " doesn't exist in database!");
            }

            Map<String, Object> updates = new HashMap<>();
            String encryptedPhone = encryptionService.encrypt(phone);
            updates.put("phone", encryptedPhone);
            documentReference.update(updates);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating the user's phone: " + e);
        }

    }

    /**
     * Retrieves the user's phone number by their ID.
     * @param userId The ID of the user whose phone number is to be retrieved.
     * @return The decrypted phone number as a String.
     * @throws RuntimeException if an error occurs while fetching the phone number or if the user does not exist.
     */
    @Override
    public String getUserPhoneNumber(String userId) throws RuntimeException{
        try {
            DocumentReference documentReference = firestore.collection(USER_COLLECTION).document(userId);
            DocumentSnapshot documentSnapshot = documentReference.get().get();

            if (!documentSnapshot.exists()) {
                throw new RuntimeException("User with id: " + userId + " doesn't exist in database!");
            }

            String encryptedPhone = documentSnapshot.getString("phone");
            System.out.println("before decr" + encryptedPhone);
            if (encryptedPhone == null || encryptedPhone.isEmpty()) {
                throw new RuntimeException("User with id: " + userId + " doesn't have a phone number!");
            }

            encryptedPhone = encryptedPhone.trim();

            return encryptionService.decrypt(encryptedPhone);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting the phone number: " + e.getMessage(), e);
        }
    }
}
