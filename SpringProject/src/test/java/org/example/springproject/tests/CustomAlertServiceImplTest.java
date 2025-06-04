/**
 * CustomAlertServiceImplTest.java
 * This file is part of the Spring Project.
 * It contains unit tests for the CustomAlertServiceImpl class.
 * The tests cover methods for saving, deleting, retrieving, and updating custom alerts.
 * The tests use Mockito to mock Firestore interactions and verify the functionality of the service methods.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.tests;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.CustomAlertDTO;
import org.example.springproject.entity.CustomAlert;
import org.example.springproject.service.implementation.CustomAlertServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomAlertServiceImpl.
 * This class tests the methods of CustomAlertServiceImpl to ensure they behave as expected.
 * It uses Mockito to mock Firestore interactions and verify the functionality of the important methods from CustomAlertServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class CustomAlertServiceImplTest {
    /**
     * Mocks the Firestore to simulate Firestore interactions.
     */
    @Mock
    private Firestore firestore;

    /**
     * Mocks the CollectionReference to simulate Firestore collection.
     * These mocks are used to test the save, delete, get, and update methods of CustomAlertServiceImpl.
     */
    @Mock
    private CollectionReference collectionReference;

    /**
     * Mocks the DocumentReference class to simulate interactions with a specific document in Firestore.
     * This mock is used to test the saving and deleting of custom alerts.
     */
    @Mock
    private DocumentReference documentReference;

    /**
     * The CustomAlertServiceImpl instance that is being tested.
     * It is injected with the mocked Firestore and DocumentReference to test its methods.
     */
    @InjectMocks
    private CustomAlertServiceImpl customAlertService;

    /**
     * Tests the saveCustomAlert method of CustomAlertServiceImpl.
     * It verifies that a custom alert can be saved successfully and that the returned DTO contains the expected values.
     * It also checks that the correct Firestore methods are called during the save operation.
     */
    @Test
    void shouldSaveCustomAlert() throws Exception {
        CustomAlert customAlert = new CustomAlert("user1","room1","sensor1","DHT22","temperature","<",20F,"Temperature alert!");

        // Mocks
        when(firestore.collection("custom_alerts")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);

        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        when(documentReference.set(customAlert)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));
        when(documentReference.getId()).thenReturn("mockAlertId123");

        // Act
        CustomAlertDTO result = customAlertService.saveCustomAlert(customAlert);

        // Assert
        assertNotNull(result);
        assertEquals("mockAlertId123", result.getId());
        assertEquals("user1", result.getUserId());
        assertEquals("room1", result.getRoomId());
        assertEquals("sensor1", result.getSensorId());
        assertEquals("DHT22", result.getSensorType());
        assertEquals("temperature", result.getParameter());
        assertEquals("<", result.getCondition());
        assertEquals(20F, result.getThreshold());
        assertEquals("Temperature alert!", result.getMessage());

        // Verify interactions
        verify(firestore).collection("custom_alerts");
        verify(collectionReference).document();
        verify(documentReference).set(customAlert);
        verify(writeResultFuture).get();
    }

    /**
     * Tests the deleteAlertById method of CustomAlertServiceImpl.
     * It verifies that a custom alert can be deleted successfully by its ID and that the returned DTO contains the expected values.
     * It also checks that the correct Firestore methods are called during the delete operation.
     */
    @Test
    void shouldDeleteCustomAlertByIdSuccessfully() throws Exception {
        // Arrange
        String alertId = "alert123";
        CustomAlert customAlert = new CustomAlert("user1", "room1", "sensor1", "DHT22", "temperature", ">", 30F, "High temp");

        DocumentReference docRef = mock(DocumentReference.class);
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        ApiFuture<DocumentSnapshot> getFuture = mock(ApiFuture.class);
        ApiFuture<WriteResult> deleteFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);

        when(firestore.collection("custom_alerts")).thenReturn(collectionReference);
        when(collectionReference.document(alertId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(getFuture);
        when(getFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(CustomAlert.class)).thenReturn(customAlert);

        when(docRef.delete()).thenReturn(deleteFuture);
        when(deleteFuture.get()).thenReturn(writeResult);

        // Act
        CustomAlertDTO result = customAlertService.deleteAlertById(alertId);

        // Assert
        assertNotNull(result);
        assertEquals(alertId, result.getId());
        assertEquals("user1", result.getUserId());
        assertEquals("room1", result.getRoomId());
        assertEquals("sensor1", result.getSensorId());
        assertEquals("DHT22", result.getSensorType());
        assertEquals("temperature", result.getParameter());
        assertEquals(">", result.getCondition());
        assertEquals(30F, result.getThreshold());
        assertEquals("High temp", result.getMessage());

        // Verify Firestore interactions
        verify(firestore).collection("custom_alerts");
        verify(collectionReference).document(alertId);
        verify(docRef).get();
        verify(docRef).delete();
    }

    /**
     * Tests the getAllCustomAlerts method of CustomAlertServiceImpl.
     * It verifies that all custom alerts can be retrieved successfully and that the returned list contains the expected values.
     * It also checks that the correct Firestore methods are called during the retrieval operation.
     */
    @Test
    void shouldGetAllCustomAlerts() throws Exception {
        // Arrange
        CustomAlert customAlert1 = new CustomAlert("user1", "room1", "sensor1", "DHT22", "temperature", ">", 25F, "Alert 1");
        CustomAlert customAlert2 = new CustomAlert("user2", "room2", "sensor2", "MQ2", "gas", ">", 100F, "Alert 2");

        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

        when(doc1.getId()).thenReturn("alert1");
        when(doc2.getId()).thenReturn("alert2");
        when(doc1.toObject(CustomAlert.class)).thenReturn(customAlert1);
        when(doc2.toObject(CustomAlert.class)).thenReturn(customAlert2);

        List<QueryDocumentSnapshot> documents = List.of(doc1, doc2);

        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(documents);

        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        when(future.get()).thenReturn(querySnapshot);

        when(firestore.collection("custom_alerts")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(future);

        // Act
        List<CustomAlertDTO> result = customAlertService.getAllCustomAlerts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("alert1", result.get(0).getId());
        assertEquals("user1", result.get(0).getUserId());
        assertEquals("room1", result.get(0).getRoomId());
        assertEquals("sensor1", result.get(0).getSensorId());

        assertEquals("alert2", result.get(1).getId());
        assertEquals("user2", result.get(1).getUserId());
        assertEquals("room2", result.get(1).getRoomId());
        assertEquals("sensor2", result.get(1).getSensorId());

        // Verify
        verify(firestore).collection("custom_alerts");
        verify(collectionReference).get();
        verify(future).get();
    }

    /**
     * Tests the getAllCustomAlertsBySensorId method of CustomAlertServiceImpl.
     * It verifies that all custom alerts for a specific sensor can be retrieved successfully and that the returned list contains the expected values.
     * It also checks that the correct Firestore methods are called during the retrieval operation.
     */
    @Test
    void shouldGetAllCustomAlertsBySensorId() throws Exception {
        // Arrange
        String sensorId = "sensor1";

        CustomAlert customAlert1 = new CustomAlert("user1", "room1", sensorId, "DHT22", "temperature", ">", 25F, "Alert 1");
        CustomAlert customAlert2 = new CustomAlert("user2", "room2", sensorId, "DHT22", "temperature", "<", 15F, "Alert 2");

        List<CustomAlert> customAlerts = List.of(customAlert1, customAlert2);

        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(querySnapshot.toObjects(CustomAlert.class)).thenReturn(customAlerts);

        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        when(future.get()).thenReturn(querySnapshot);

        Query query = mock(Query.class);
        when(query.get()).thenReturn(future);

        when(firestore.collection("custom_alerts")).thenReturn(collectionReference);
        when(collectionReference.whereEqualTo("sensorId", sensorId)).thenReturn(query);

        // Act
        List<CustomAlert> result = customAlertService.getAllCustomAlertsBySensorId(sensorId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("room1", result.get(0).getRoomId());
        assertEquals("room2", result.get(1).getRoomId());

        // Verify
        verify(firestore).collection("custom_alerts");
        verify(collectionReference).whereEqualTo("sensorId", sensorId);
        verify(query).get();
        verify(future).get();
    }

    /**
     * Tests the getCustomAlertById method of CustomAlertServiceImpl.
     * It verifies that a custom alert can be retrieved successfully by its ID and that the returned DTO contains the expected values.
     * It also checks that the correct Firestore methods are called during the retrieval operation.
     */
    @Test
    void shouldGetCustomAlertByIdSuccessfully() throws Exception {
        // Arrange
        String alertId = "alert123";
        CustomAlert customAlert = new CustomAlert("user1", "room1", "sensor1", "DHT22", "temperature", ">", 30F, "Alert message");

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentSnapshot.toObject(CustomAlert.class)).thenReturn(customAlert);

        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        when(future.get()).thenReturn(documentSnapshot);

        when(firestore.collection("custom_alerts")).thenReturn(collectionReference);
        when(collectionReference.document(alertId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(future);

        // Act
        CustomAlertDTO result = customAlertService.getCustomAlertById(alertId);

        // Assert
        assertNotNull(result);
        assertEquals("user1", result.getUserId());
        assertEquals("room1", result.getRoomId());
        assertEquals("sensor1", result.getSensorId());
        assertEquals("DHT22", result.getSensorType());
        assertEquals("temperature", result.getParameter());
        assertEquals(">", result.getCondition());
        assertEquals(30F, result.getThreshold());
        assertEquals("Alert message", result.getMessage());

        // Verify
        verify(firestore).collection("custom_alerts");
        verify(collectionReference).document(alertId);
        verify(documentReference).get();
    }

    /**
     * Tests the getCustomAlertsByUserId method of CustomAlertServiceImpl.
     * It verifies that custom alerts can be retrieved successfully by user ID and that the returned list contains the expected values.
     * It also checks that the correct Firestore methods are called during the retrieval operation.
     */
    @Test
    void shouldGetCustomAlertsByUserIdSuccessfully() throws Exception {
        // Arrange
        String userId = "user1";
        CustomAlert customAlert = new CustomAlert("user1", "room1", "sensor1", "DHT22", "temperature", ">", 30F, "High temp");

        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);

        when(documentSnapshot.getId()).thenReturn("alert123");
        when(documentSnapshot.toObject(CustomAlert.class)).thenReturn(customAlert);
        when(querySnapshot.getDocuments()).thenReturn(List.of(documentSnapshot));

        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        when(future.get()).thenReturn(querySnapshot);

        CollectionReference alertCollection = mock(CollectionReference.class);
        Query query = mock(Query.class);

        when(firestore.collection("custom_alerts")).thenReturn(alertCollection);
        when(alertCollection.whereEqualTo("userId", userId)).thenReturn(query);
        when(query.get()).thenReturn(future);

        // Act
        List<CustomAlertDTO> result = customAlertService.getCustomAlertsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        CustomAlertDTO dto = result.get(0);
        assertEquals("user1", dto.getUserId());
        assertEquals("room1", dto.getRoomId());
        assertEquals("sensor1", dto.getSensorId());
        assertEquals("DHT22", dto.getSensorType());
        assertEquals("temperature", dto.getParameter());
        assertEquals(">", dto.getCondition());
        assertEquals(30F, dto.getThreshold());
        assertEquals("High temp", dto.getMessage());

        // Verify
        verify(firestore).collection("custom_alerts");
        verify(alertCollection).whereEqualTo("userId", userId);
        verify(query).get();
    }

    /**
     * Tests the updateCustomAlert method of CustomAlertServiceImpl.
     * It verifies that a custom alert can be updated successfully and that the returned DTO contains the expected values.
     * It also checks that the correct Firestore methods are called during the update operation.
     */
    @Test
    void shouldUpdateCustomAlertSuccessfully() throws Exception {
        // Arrange
        String alertId = "alert123";

        CustomAlert existingAlert = new CustomAlert("user1", "room1", "sensor1", "DHT22", "temperature", ">", 30F, "High temp");
        CustomAlert updatedAlert = new CustomAlert("user2", "room2", "sensor2", "MQ5", "humidity", "<", 20F, "Low humidity");

        DocumentReference docRef = mock(DocumentReference.class);
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        ApiFuture<DocumentSnapshot> getFuture = mock(ApiFuture.class);
        ApiFuture<WriteResult> setFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);

        when(firestore.collection("custom_alerts")).thenReturn(collectionReference);
        when(collectionReference.document(alertId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(getFuture);
        when(getFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(CustomAlert.class)).thenReturn(existingAlert);

        when(docRef.set(any(CustomAlert.class))).thenReturn(setFuture);
        when(setFuture.get()).thenReturn(writeResult);

        // Act
        CustomAlertDTO result = customAlertService.updateCustomAlert(alertId, updatedAlert);

        // Assert
        assertNotNull(result);
        assertEquals(alertId, result.getId());
        assertEquals("user2", result.getUserId());
        assertEquals("room2", result.getRoomId());
        assertEquals("sensor2", result.getSensorId());
        assertEquals("MQ5", result.getSensorType());
        assertEquals("humidity", result.getParameter());
        assertEquals("<", result.getCondition());
        assertEquals(20F, result.getThreshold());
        assertEquals("Low humidity", result.getMessage());

        // Verify Firestore interactions
        verify(firestore).collection("custom_alerts");
        verify(collectionReference).document(alertId);
        verify(docRef).get();
        verify(docRef).set(any(CustomAlert.class));
    }




}
