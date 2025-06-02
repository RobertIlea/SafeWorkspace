/**
 * AlertServiceImplTest.java
 * This file is part of the Spring Project.
 * It contains unit tests for the AlertServiceImpl class.
 * The tests cover methods for saving, deleting, retrieving, and updating alerts.
 * The tests use Mockito to mock Firestore interactions and verify the functionality of the service methods.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.tests;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.AlertDTO;
import org.example.springproject.entity.Alert;
import org.example.springproject.service.implementation.AlertServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * AlertServiceImplTest class contains unit tests for the AlertServiceImpl class.
 * It tests the functionality of all the methods in the AlertServiceImpl class.
 * This class uses Mockito to mock the Firestore interactions and verify the behavior of the service methods.
 */
@ExtendWith(MockitoExtension.class)
public class AlertServiceImplTest {
    /**
     * Mocks the Firestore to simulate Firestore interactions.
     */
    @Mock
    private Firestore firestore;

    /**
     * Mocks for Firestore collection references.
     */
    @Mock
    private CollectionReference collectionReference;

    /**
     * Mocks for Firestore document reference.
     */
    @Mock
    private DocumentReference documentReference;

    /**
     * The AlertServiceImpl instance to be tested.
     * It is injected with the mocked Firestore components.
     */
    @InjectMocks
    private AlertServiceImpl alertService;

    /**
     * Test method to verify the successful saving of an alert.
     * It mocks the Firestore interactions and checks if the alert is saved correctly.
     * The test asserts that the returned AlertDTO contains the expected values.
     * It also verifies that the Firestore methods are called with the correct parameters.
     */
    @Test
    void shouldSaveAlertSuccessfully() throws Exception {
        // Arrange
        Alert alert = new Alert();
        alert.setRoomId("room123");
        alert.setSensorId("sensorABC");
        alert.setTimestamp(Timestamp.now());
        alert.setSensorType("DHT22");
        alert.setData(Map.of("temperature", 32F));
        alert.setMessage("Temperature exceeded");

        // Mocks
        when(firestore.collection("alerts")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);

        ApiFuture<WriteResult> writeFuture = mock(ApiFuture.class);
        when(documentReference.set(alert)).thenReturn(writeFuture);
        when(writeFuture.get()).thenReturn(mock(WriteResult.class));
        when(documentReference.getId()).thenReturn("mockAlertId");

        // Act
        AlertDTO result = alertService.saveAlert(alert);

        // Assert
        assertNotNull(result);
        assertEquals("mockAlertId", result.getAlertId());
        assertEquals("room123", result.getRoomId());
        assertEquals("sensorABC", result.getSensorId());
        assertEquals("DHT22", result.getSensorType());
        assertEquals("Temperature exceeded", result.getMessage());
        assertEquals(32F, result.getData().get("temperature"));

        // Verify Firestore interactions
        verify(firestore).collection("alerts");
        verify(collectionReference).document();
        verify(documentReference).set(alert);
        System.out.println("Test of shouldSaveAlertSuccessfully() was successful!");
    }

    /**
     * Test method to get all alerts from the Firestore database.
     * It mocks the Firestore interactions to retrieve alerts and checks if the returned list of AlertDTOs contains the expected values.
     * This test ensures that the service can retrieve all alerts for a specific room.
     * It also verifies that the Firestore methods are called with the correct parameters.
     */
    @Test
    void shouldGetAllAlertsSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";

        // Mock document snapshot
        QueryDocumentSnapshot docSnapshot = mock(QueryDocumentSnapshot.class);
        when(docSnapshot.getId()).thenReturn("alert123");
        when(docSnapshot.getString("roomId")).thenReturn(roomId);
        when(docSnapshot.getString("sensorId")).thenReturn("sensor456");
        when(docSnapshot.getTimestamp("timestamp")).thenReturn(Timestamp.now());
        when(docSnapshot.getString("sensorType")).thenReturn("DHT22");
        when(docSnapshot.getString("message")).thenReturn("Temperature too high");

        Map<String, Object> rawData = Map.of("temperature", 33.5);
        when(docSnapshot.get("data")).thenReturn(rawData);

        List<QueryDocumentSnapshot> docs = List.of(docSnapshot);

        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(docs);

        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        when(future.get()).thenReturn(querySnapshot);

        CollectionReference alertCollection = mock(CollectionReference.class);
        Query query = mock(Query.class);

        when(firestore.collection("alerts")).thenReturn(alertCollection);
        when(alertCollection.whereEqualTo("roomId", roomId)).thenReturn(query);
        when(query.get()).thenReturn(future);

        // Act
        List<AlertDTO> result = alertService.getAlerts(roomId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        AlertDTO alert = result.get(0);
        assertEquals("alert123", alert.getAlertId());
        assertEquals("sensor456", alert.getSensorId());
        assertEquals("DHT22", alert.getSensorType());
        assertEquals("Temperature too high", alert.getMessage());
        assertEquals(33.5f, alert.getData().get("temperature"));

        // Verify Firestore interactions
        verify(firestore).collection("alerts");
        verify(alertCollection).whereEqualTo("roomId", roomId);
        verify(query).get();
        System.out.println("Test of shouldGetAllAlertsSuccessfully() was successful!");
    }

    /**
     * Test method to get alerts by room ID and date.
     * It mocks the Firestore interactions to retrieve alerts for a specific room and date.
     * The test asserts that the returned list of AlertDTOs contains the expected values.
     * It also verifies that the Firestore methods are called with the correct parameters.
     * This test ensures that the service can filter alerts based on room ID and a specific date range.
     */
    @Test
    void shouldGetAlertsByRoomAndDateSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";

        Date selectedDate = new GregorianCalendar(2024, Calendar.JUNE, 1).getTime();

        // Mock document snapshot
        QueryDocumentSnapshot docSnapshot = mock(QueryDocumentSnapshot.class);
        when(docSnapshot.getId()).thenReturn("alert123");
        when(docSnapshot.getString("roomId")).thenReturn(roomId);
        when(docSnapshot.getString("sensorId")).thenReturn("sensor456");
        when(docSnapshot.getTimestamp("timestamp")).thenReturn(Timestamp.now());
        when(docSnapshot.getString("sensorType")).thenReturn("DHT22");
        when(docSnapshot.getString("message")).thenReturn("High humidity");

        Map<String, Object> rawData = Map.of("humidity", 80);
        when(docSnapshot.get("data")).thenReturn(rawData);

        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(List.of(docSnapshot));

        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        when(future.get()).thenReturn(querySnapshot);

        CollectionReference alertCollection = mock(CollectionReference.class);
        Query roomQuery = mock(Query.class);
        Query startQuery = mock(Query.class);
        Query endQuery = mock(Query.class);

        when(firestore.collection("alerts")).thenReturn(alertCollection);
        when(alertCollection.whereEqualTo("roomId", roomId)).thenReturn(roomQuery);
        when(roomQuery.whereGreaterThanOrEqualTo(eq("timestamp"), any(Date.class))).thenReturn(startQuery);
        when(startQuery.whereLessThanOrEqualTo(eq("timestamp"), any(Date.class))).thenReturn(endQuery);
        when(endQuery.get()).thenReturn(future);

        // Act
        List<AlertDTO> result = alertService.getAlertsByRoomAndDate(roomId, selectedDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        AlertDTO alert = result.get(0);
        assertEquals("alert123", alert.getAlertId());
        assertEquals("sensor456", alert.getSensorId());
        assertEquals("DHT22", alert.getSensorType());
        assertEquals("High humidity", alert.getMessage());
        assertEquals(80f, alert.getData().get("humidity"));

        // Verify query chain
        verify(firestore).collection("alerts");
        verify(alertCollection).whereEqualTo("roomId", roomId);
        verify(roomQuery).whereGreaterThanOrEqualTo(eq("timestamp"), any(Date.class));
        verify(startQuery).whereLessThanOrEqualTo(eq("timestamp"), any(Date.class));
        verify(endQuery).get();
        System.out.println("Test of shouldGetAlertsByRoomAndDateSuccessfully() was successful!");

    }

}
