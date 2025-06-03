/**
 * UserServiceImplTest.java
 * This file is part of the Spring Project.
 * It contains unit tests for the UserServiceImpl class.
 * This class is responsible for testing the methods of UserServiceImpl,
 * This test class uses Mockito to mock dependencies and verify interactions.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.tests;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;
import org.example.springproject.service.implementation.UserServiceImpl;
import org.example.springproject.util.EncryptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for UserServiceImpl.
 * This class contains unit tests for the methods in UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    /**
     * Mocked Firestore instance.
     */
    @Mock
    private Firestore firestore;

    /**
     * Mocked Firestore collection reference.
     */
    @Mock
    private CollectionReference collectionReference;

    /**
     * Mocked Firestore document reference.
     */
    @Mock
    private DocumentReference documentReference;

    /**
     * Mocked PasswordEncoder instance.
     * This is used to encode passwords before saving them to the database.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EncryptionService encryptionService;

    /**
     * The UserServiceImpl instance that is being tested.
     * It is annotated with @InjectMocks to inject the mocked dependencies.
     * @Spy is used to allow partial mocking of the class.
     */
    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    /**
     * This method is called before each test to set up the necessary conditions.
     * It initializes the Firestore collection reference to the "users" collection.
     */
    @Test
    void shouldPassValidationForValidUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setName("validName");
        user.setPassword("validPassword");

        assertDoesNotThrow(() -> userService.userVerification(user));
    }

    /**
     * This method tests the userVerification method of UserServiceImpl.
     * It checks if the method throws an exception for an invalid email format.
     */
    @Test
    void shouldThrowExceptionForInvalidEmail(){
        User user = new User();
        user.setEmail("invalid-email");
        user.setName("validName");
        user.setPassword("validPassword");

        Exception ex = assertThrows(RuntimeException.class, () -> userService.userVerification(user));
        assertEquals("Invalid email format!", ex.getMessage());
    }

    /**
     * This method tests the userVerification method of UserServiceImpl.
     * It checks if the method throws an exception for an invalid name length.
     */
    @Test
    void shouldThrowExceptionForShortName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("abc");
        user.setPassword("strongPass123");

        Exception ex = assertThrows(RuntimeException.class, () -> userService.userVerification(user));
        assertEquals("Name length must be between 5 and 32", ex.getMessage());
    }

    /**
     * This method tests the userVerification method of UserServiceImpl.
     * It checks if the method throws an exception for a short password.
     */
    @Test
    void shouldThrowExceptionForShortPassword() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("validname");
        user.setPassword("short");

        Exception ex = assertThrows(RuntimeException.class, () -> userService.userVerification(user));
        assertEquals("Length of the password must be greater than 8 characters", ex.getMessage());
    }

    /**
     * This method tests the AddUser method of UserServiceImpl.
     * It checks if a user can be added successfully to the Firestore database.
     * It mocks the necessary Firestore interactions and verifies that the user is added correctly.
     */
    @Test
    void shouldAddUserSuccessfully() throws Exception {
        User user = new User();
        user.setPassword("password");
        user.setEmail("email@gmail.com");
        user.setName("username");

        // Mock Firestore references
        when(firestore.collection("users")).thenReturn(collectionReference);

        // Mock email check
        Query emailQuery = mock(Query.class);
        when(collectionReference.whereEqualTo("email", user.getEmail())).thenReturn(emailQuery);

        ApiFuture<QuerySnapshot> querySnapshot = mock(ApiFuture.class);
        QuerySnapshot queryResult = mock(QuerySnapshot.class);
        when(emailQuery.get()).thenReturn(querySnapshot);
        when(querySnapshot.get()).thenReturn(queryResult);
        when(queryResult.isEmpty()).thenReturn(true);

        // Mock document insert
        when(collectionReference.document()).thenReturn(documentReference);
        ApiFuture<WriteResult> writeResult = mock(ApiFuture.class);
        when(documentReference.set(any(User.class))).thenReturn(writeResult);
        when(writeResult.get()).thenReturn(mock(WriteResult.class));
        when(documentReference.getId()).thenReturn("mockId");

        // Do nothing for user verification
        doNothing().when(userService).userVerification(any());

        // Mock password encoding
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);

        // Act
        UserDTO result = userService.addUser(user);
        assertNotNull(result);
        assertEquals("username", result.getName());
        assertEquals("email@gmail.com", result.getEmail());
        assertEquals("mockId", result.getId());

        // Verify
        verify(userService).userVerification(any());
        verify(passwordEncoder).encode("password");
        verify(documentReference).set(any(User.class));
    }

    /**
     * This method tests the deleteUserById method of UserServiceImpl.
     * It checks if a user can be deleted successfully from the Firestore database.
     * It mocks the necessary Firestore interactions and verifies that the user is deleted correctly.
     */
    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        // Arrange
        String userId = "user123";

        User user = new User();
        user.setName("username");
        user.setEmail("email@gmail.com");

        // Mock Firestore
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document(userId)).thenReturn(documentReference);

        // Mock Snapshot
        ApiFuture<DocumentSnapshot> querySnapshot = mock(ApiFuture.class);
        DocumentSnapshot queryResult = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(querySnapshot);
        when(querySnapshot.get()).thenReturn(queryResult);
        when(queryResult.exists()).thenReturn(true);
        when(queryResult.toObject(User.class)).thenReturn(user);

        // Mock delete call
        ApiFuture<WriteResult> deleteFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);
        when(documentReference.delete()).thenReturn(deleteFuture);
        when(deleteFuture.get()).thenReturn(writeResult);

        // Act
        UserDTO result = userService.deleteUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals("username", result.getName());
        assertEquals(userId, result.getId());
        assertEquals("email@gmail.com", result.getEmail());

        // Verify Firestore interactions
        verify(firestore).collection("users");
        verify(collectionReference).document(userId);
        verify(documentReference).get();
        verify(documentReference).delete();

    }

    /**
     * This method tests the updateUser method of UserServiceImpl.
     * It checks if a user can be updated successfully in the Firestore database.
     * It mocks the necessary Firestore interactions and verifies that the user is updated correctly.
     */
    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        String userId = "user123";

        // User that already exists
        User currentUser = new User();
        currentUser.setName("username");
        currentUser.setEmail("email@gmail.com");
        currentUser.setPassword("password");

        // New user
        User updatedUser = new User();
        updatedUser.setName("updatedName");
        updatedUser.setEmail("updatedemail@gmail.com");
        updatedUser.setPassword("updatedPassword");

        // Mock Firestore
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document(userId)).thenReturn(documentReference);

        // Mock document snapshot
        ApiFuture<DocumentSnapshot> querySnapshot = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(querySnapshot);
        when(querySnapshot.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(User.class)).thenReturn(currentUser);

        // Mock password encoding
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);

        // Mock Firestore write
        ApiFuture<WriteResult> writeResultApiFuture = mock(ApiFuture.class);
        when(documentReference.set(any(User.class))).thenReturn(writeResultApiFuture);
        when(writeResultApiFuture.get()).thenReturn(mock(WriteResult.class));

        // Bypass userVerification logic because it's not the focus of this test
        doNothing().when(userService).userVerification(any());

        // Act
        UserDTO result = userService.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("updatedName", result.getName());
        assertEquals("updatedemail@gmail.com", result.getEmail());

        // Verify interactions
        verify(firestore).collection("users");
        verify(collectionReference).document(userId);
        verify(documentReference).get();
        verify(documentReference).set(any(User.class));
        verify(passwordEncoder).encode("updatedPassword");
        verify(userService).userVerification(any());
    }

    /**
     * This method tests the getUserById method of UserServiceImpl.
     * It checks if a user can be retrieved successfully from the Firestore database by their ID.
     * It mocks the necessary Firestore interactions and verifies that the user is retrieved correctly.
     */
    @Test
    void shouldGetUserByIdSuccessfully() throws Exception {
        // Arrange
        String userId = "user123";

        User user = new User();
        user.setName("username");
        user.setEmail("email@gmail.com");
        user.setPassword("password");

        // Mock Firestore references
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document(userId)).thenReturn(documentReference);

        ApiFuture<DocumentSnapshot> querySnapshot = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(querySnapshot);
        when(querySnapshot.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(User.class)).thenReturn(user);

        // Act
        UserDTO result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("username", result.getName());
        assertEquals("email@gmail.com", result.getEmail());

        // Verify Firestore interactions
        verify(firestore).collection("users");
        verify(collectionReference).document(userId);
        verify(documentReference).get();
    }

    @Test
    void shouldUpdateUserPhoneSuccessfully() throws Exception {
        // Arrange
        String userId = "user123";
        String plainPhone = "0745123456";
        String encryptedPhone = "encrypted_0745123456";

        // Mock Firestore references
        when(firestore.collection("users")).thenReturn(collectionReference);
        when(collectionReference.document(userId)).thenReturn(documentReference);

        // Mock document snapshot
        ApiFuture<DocumentSnapshot> getFuture = mock(ApiFuture.class);
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(getFuture);
        when(getFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);

        // Mock encryption
        when(encryptionService.encrypt(plainPhone)).thenReturn(encryptedPhone);

        // Mock update call
        ApiFuture<WriteResult> updateFuture = mock(ApiFuture.class);
        when(documentReference.update(any(Map.class))).thenReturn(updateFuture);

        // Act
        userService.updateUserPhone(userId, plainPhone);

        // Assert
        verify(firestore).collection("users");
        verify(collectionReference).document(userId);
        verify(documentReference).get();
        verify(documentReference).update(argThat(map ->
                map.containsKey("phone") && map.get("phone").equals(encryptedPhone)
        ));
        verify(encryptionService).encrypt(plainPhone);
    }




}
