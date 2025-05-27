package org.example.springproject.service.implementation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;
import org.example.springproject.service.UserService;
import org.example.springproject.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private Firestore firestore;
    private final PasswordEncoder passwordEncoder;
    private static final String ROOM_COLLECTION = "rooms";
    private static final String USER_COLLECTION = "users";

    public UserServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    private boolean isEmailValid(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void userVerification(User user) {
        if(!isEmailValid(user.getEmail())){
            throw new RuntimeException("Invalid email format!");
        }
        if(user.getName().length() < 5 || user.getName().length() > 32){
            throw new RuntimeException("Name length must be between 5 and 20");
        }
        if(user.getPassword().isEmpty() || user.getPassword().length() < 8 ){
            throw new RuntimeException("Length of the password must be greater than 8 characters");
        }
    }

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
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding a new user: " + e.getMessage(), e);
        }
    }

    @Override
    public UserDTO deleteUserbyId(String id) throws RuntimeException{
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
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting the user: " + e.getMessage(), e);
        }
    }

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
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating the user: " + e.getMessage(), e);
        }
    }

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
        }catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during Firestore operation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the users: " + e.getMessage(), e);
        }
    }

    @Override
    public UserDTO getUserById(String id){
        try{
            DocumentReference documentReference = firestore.collection(USER_COLLECTION).document(id);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();

            User user = null;
            if(!document.exists()){
                throw new RuntimeException("User with id: " + id + " doesn't exist in database!");
            }
            user = document.toObject(User.class);
            return new UserDTO(id,user.getName(),user.getEmail());

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByEmail(String email){
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

            return new User(user.getName(),user.getEmail(),user.getPassword());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUserIdByEmail(String email){
        try{
            ApiFuture<QuerySnapshot> future = firestore.collection(USER_COLLECTION).whereEqualTo("email", email).get();
            QuerySnapshot querySnapshot = future.get();

            if(querySnapshot.isEmpty()){
                throw new RuntimeException("User with email: " + email + " doesn't exist in database!");
            }

            QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
            return document.getId();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDTO getUserByRoomId(String roomId){
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

            return new UserDTO(roomId,user.getName(),user.getEmail());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
