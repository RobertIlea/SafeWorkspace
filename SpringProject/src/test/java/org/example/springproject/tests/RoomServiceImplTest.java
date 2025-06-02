/**
 * RoomServiceImplTest.java
 * This file is part of the Spring Project.
 * It contains unit tests for the RoomServiceImpl class.
 * The tests cover methods for saving, deleting, retrieving, and updating rooms.
 * The tests use Mockito to mock Firestore interactions and verify the functionality of the service methods.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.tests;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Room;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.implementation.RoomServiceImpl;
import org.example.springproject.service.implementation.SensorServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RoomServiceImpl class.
 * This class tests the methods of RoomServiceImpl using Mockito to mock dependencies.
 */
@ExtendWith(MockitoExtension.class)
public class RoomServiceImplTest {
    /**
     * Mocked Firestore instance to simulate Firestore operations.
     */
    @Mock
    private Firestore firestore;

    /**
     * Mocked CollectionReference to simulate Firestore collection operations.
     */
    @Mock
    private CollectionReference collectionReference;

    /**
     * Mocked DocumentReference to simulate Firestore document operations.
     */
    @Mock
    private DocumentReference documentReference;

    /**
     * The RoomServiceImpl instance being tested, with dependencies injected.
     * It is annotated with @InjectMocks to inject the mocked dependencies.
     * Uses @Spy to allow partial mocking of the class.
     */
    @Spy
    @InjectMocks
    private RoomServiceImpl roomServiceImpl;

    /**
     * Mocked SensorServiceImpl to simulate sensor-related operations.
     * This is used to test methods that involve sensors in rooms.
     */
    @Mock
    private SensorServiceImpl sensorServiceImpl;

    /**
     * Test to ensure that the addRoom method works correctly.
     * It mocks the Firestore interactions and verifies that a room can be added successfully.
     * This test checks that the room is created with the correct properties and that the Firestore methods are called as expected.
     */
    @Test
    void shouldAddRoomSuccessfully() throws Exception {
        // Arrange
        Room room = new Room();
        room.setName("testRoom");
        room.setUserId("user123");

        // Mock Firestore references
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);

        // Mock document snapshot
        ApiFuture<DocumentSnapshot> snapshotFuture = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(snapshotFuture);
        when(snapshotFuture.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(false);

        // Mock write result
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);
        when(documentReference.set(room)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        // Act
        RoomDTO roomDTO = roomServiceImpl.addRoom(room);

        // Assert
        assertNotNull(roomDTO);
        assertEquals("testRoom", roomDTO.getName());
        assertEquals("user123", roomDTO.getUserId());
    }

    /**
     * Test to ensure that the deleteRoomById method works correctly.
     * It mocks the Firestore interactions and verifies that a room can be deleted successfully.
     * This test checks that the room is deleted and returns the correct RoomDTO.
     */
    @Test
    void shouldDeleteRoomById() throws Exception {
        String roomId = "abc123";

        // Setup room
        Room room = new Room();
        room.setName("Test Room");
        room.setUserId("user123");

        // Mock Firestore calls
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);

        // Snapshot mocking
        ApiFuture<DocumentSnapshot> snapshotFuture = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(snapshotFuture);
        when(snapshotFuture.get()).thenReturn(snapshot);

        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Room.class)).thenReturn(room);
        when(snapshot.get("sensors")).thenReturn(null);

        // Mock delete future
        ApiFuture<WriteResult> deleteFuture = mock(ApiFuture.class);
        when(documentReference.delete()).thenReturn(deleteFuture);
        when(deleteFuture.get()).thenReturn(mock(WriteResult.class));

        // Run method
        RoomDTO result = roomServiceImpl.deleteRoomById(roomId);

        // Assertions
        assertNotNull(result);
        assertEquals("abc123", result.getId());
        assertEquals("Test Room", result.getName());
        assertEquals("user123", result.getUserId());

        // Verify delete was called
        verify(documentReference).delete();
    }

    /**
     * Test to ensure that the updateRoom method works correctly.
     * It mocks the Firestore interactions and verifies that a room can be updated successfully.
     * This test checks that the room is updated with the new properties and returns the correct RoomDTO.
     */
    @Test
    void shouldUpdateRoomSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";
        Room room = new Room();
        room.setName("Updated Room");
        room.setUserId("user456");

        // Mock Firestore references
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);

        // Mock document snapshot
        ApiFuture<DocumentSnapshot> snapshotFuture = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(snapshotFuture);
        when(snapshotFuture.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Room.class)).thenReturn(room);

        // Mock write result
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);
        when(documentReference.set(room)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        // Act
        RoomDTO updatedRoomDTO = roomServiceImpl.updateRoom(roomId, room);

        // Assert
        assertNotNull(updatedRoomDTO);
        assertEquals("Updated Room", updatedRoomDTO.getName());
        assertEquals("user456", updatedRoomDTO.getUserId());
    }

    /**
     * Test to ensure that the getRoomById method works correctly.
     * It mocks the Firestore interactions and verifies that a room can be retrieved successfully by its ID.
     * This test checks that the room is returned with the correct properties.
     */
    @Test
    void shouldGetRoomByIdSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";
        Room room = new Room();
        room.setName("Test Room");
        room.setUserId("user789");

        // Mock Firestore references
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);

        // Mock document snapshot
        ApiFuture<DocumentSnapshot> snapshotFuture = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(snapshotFuture);
        when(snapshotFuture.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Room.class)).thenReturn(room);

        // Act
        RoomDTO roomDTO = roomServiceImpl.getRoomById(roomId);

        // Assert
        assertNotNull(roomDTO);
        assertEquals("Test Room", roomDTO.getName());
        assertEquals("user789", roomDTO.getUserId());
    }

    /**
     * Test to ensure that the getAllRooms method works correctly.
     * It mocks the Firestore interactions and verifies that all rooms can be retrieved successfully.
     * This test checks that a list of RoomDTOs is returned with the correct properties.
     */
    @Test
    void shouldAddSensorToRoomSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";
        String sensorId = "sensor456";

        // Mock Firestore references
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);

        // Mock room snapshot
        ApiFuture<DocumentSnapshot> snapshotFuture = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(snapshotFuture);
        when(snapshotFuture.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getString("name")).thenReturn("Test Room");
        when(snapshot.getString("userId")).thenReturn("user789");
        when(snapshot.get("sensors")).thenReturn(new ArrayList<>());

        // Mock sensor snapshot
        DocumentReference sensorDocRef = mock(DocumentReference.class);
        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.document(sensorId)).thenReturn(sensorDocRef);
        ApiFuture<DocumentSnapshot> sensorFuture = mock(ApiFuture.class);
        DocumentSnapshot sensorDocSnapshot = mock(DocumentSnapshot.class);
        when(sensorDocRef.get()).thenReturn(sensorFuture);
        when(sensorFuture.get()).thenReturn(sensorDocSnapshot);
        when(sensorDocSnapshot.exists()).thenReturn(true);

        Sensor testSensor = new Sensor();
        testSensor.setPort(2);
        testSensor.setSensorType("DHT22");
        testSensor.setDetails(new ArrayList<>());
        when(sensorServiceImpl.deserializeSensor(sensorDocSnapshot)).thenReturn(testSensor);

        // Mock update write result
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);
        when(documentReference.update(eq("sensors"), any())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        // Act
        RoomDTO result = roomServiceImpl.addSensorToRoom(roomId,sensorId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Room", result.getName());
        assertEquals("user789", result.getUserId());
        assertEquals(1, result.getSensors().size());
        assertEquals(sensorId, result.getSensors().get(0).getId());

        // Verify Firestore update
        verify(documentReference).update(eq("sensors"), any());

    }

    /**
     * Test to ensure that the getSensorsByRoomId method works correctly.
     * It mocks the Firestore interactions and verifies that sensors can be retrieved successfully from a room.
     * This test checks that a list of SensorDTOs is returned with the correct properties.
     */
    @Test
    void shouldGetSensorsListFromRoomSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";

        Map<String, Object> sensorMap = new HashMap<>();
        sensorMap.put("id", "sensor1");
        sensorMap.put("sensorType", "DHT22");
        sensorMap.put("port", 2);
        sensorMap.put("details", new ArrayList<>());

        List<Map<String, Object>> sensorList = List.of(sensorMap);

        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        ApiFuture<DocumentSnapshot> snapshotFuture = mock(ApiFuture.class);

        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(snapshotFuture);
        when(snapshotFuture.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.get("sensors")).thenReturn(sensorList);

        // Act
        List<SensorDTO> result = roomServiceImpl.getSensorsByRoomId(roomId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        SensorDTO sensorDTO = result.get(0);
        assertNotNull(sensorDTO);
        assertEquals("sensor1", sensorDTO.getId());
        assertEquals("DHT22", sensorDTO.getSensorType());
        assertEquals(2, sensorDTO.getPort());
        assertTrue(sensorDTO.getDetails().isEmpty());

        // Verify Firestore
        verify(firestore).collection("rooms");
        verify(collectionReference).document("room123");
        verify(documentReference).get();
    }

    /**
     * Test to ensure that the UpdateRoomWithSensorData method works correctly.
     * It mocks the Firestore interactions and verifies that a room can be updated with sensor data successfully.
     * This test checks that the sensor data is added to the room's sensors and that the Firestore methods are called as expected.
     */
    @Test
    void shouldUpdateRoomWithSensorDataSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";
        String sensorId = "sensor1";

        Details newDetails = new Details();

        Map<String, Float> data = new HashMap<>();
        data.put("temperature", 23F);
        data.put("humidity", 55F);

        newDetails.setData(data);
        newDetails.setTimestamp(Timestamp.parseTimestamp("2024-06-01T12:00:00Z"));

        SensorDTO existingSensor = new SensorDTO();
        existingSensor.setId(sensorId);
        existingSensor.setPort(2);
        existingSensor.setSensorType("DHT22");
        existingSensor.setDetails(new ArrayList<>());

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName("Test Room");
        roomDTO.setUserId("user123");
        roomDTO.setSensors(List.of(existingSensor));

        SensorDTO sensorUpdate = new SensorDTO();
        sensorUpdate.setId(sensorId);
        sensorUpdate.setDetails(List.of(newDetails));

        // Mock Firestore
        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);

        ApiFuture<DocumentSnapshot> snapshotFuture = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(snapshotFuture);
        when(snapshotFuture.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(RoomDTO.class)).thenReturn(roomDTO);

        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);
        when(documentReference.set(any(RoomDTO.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);
        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        // Act
        String updateTime = roomServiceImpl.updateRoomWithSensorData(roomId, sensorUpdate);

        // Assert
        assertNotNull(updateTime);
        assertEquals(1, roomDTO.getSensors().size());
        assertEquals(23F, roomDTO.getSensors().get(0).getDetails().get(0).getData().get("temperature"));
        assertEquals(55F, roomDTO.getSensors().get(0).getDetails().get(0).getData().get("humidity"));
        assertEquals("2024-06-01T12:00:00Z", roomDTO.getSensors().get(0).getDetails().get(0).getTimestamp().toString());

        // Verify Firestore interactions
        verify(firestore).collection("rooms");
        verify(collectionReference).document(roomId);
        verify(documentReference).get();
        verify(documentReference).set(roomDTO);
    }

    /**
     * Test to ensure that the AssignRoomToUser method works correctly.
     * It mocks the Firestore interactions and verifies that a room can be assigned to a user successfully.
     * This test checks that the room's user ID is updated, the name is changed, and sensors are added correctly.
     * This test also verifies that the Firestore methods are called as expected.
     */
    @Test
    void shouldAssignRoomToUserSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";
        String userId = "user456";
        String newName = "New Room Name";
        List<String> sensorIds = List.of("sensor1", "sensor2");

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName("Old Name");
        roomDTO.setUserId(""); // A room that is not assigned yet
        roomDTO.setSensors(new ArrayList<>());

        doReturn(roomDTO).when(roomServiceImpl).getRoomById(roomId);

        SensorDTO sensor1 = new SensorDTO();
        sensor1.setId(sensorIds.get(0));
        sensor1.setSensorType("DHT22");

        SensorDTO sensor2 = new SensorDTO();
        sensor2.setId(sensorIds.get(1));
        sensor2.setSensorType("MQ5");

        when(sensorServiceImpl.getSensorById(sensorIds.get(0))).thenReturn(sensor1);
        when(sensorServiceImpl.getSensorById(sensorIds.get(1))).thenReturn(sensor2);

        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);

        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);
        when(documentReference.set(any(RoomDTO.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        // Act
        RoomDTO updatedRoom = roomServiceImpl.assignRoomToUser(roomId, userId, newName, sensorIds);

        // Assert
        assertNotNull(updatedRoom);
        assertEquals(newName, updatedRoom.getName());
        assertEquals(userId, updatedRoom.getUserId());
        assertEquals(sensorIds.size(), updatedRoom.getSensors().size());
        assertEquals("sensor1", updatedRoom.getSensors().get(0).getId());
        assertEquals("sensor2", updatedRoom.getSensors().get(1).getId());
        assertEquals("DHT22", updatedRoom.getSensors().get(0).getSensorType());
        assertEquals("MQ5", updatedRoom.getSensors().get(1).getSensorType());

        // Verify Firestore interactions
        verify(firestore).collection("rooms");
        verify(collectionReference).document(roomId);
        verify(documentReference).set(any(RoomDTO.class));

        // Verify that firestore.get() from getRoomById was not called
        verify(documentReference, never()).get();
    }

    /**
     * Test to ensure that the removeUserFromRoom method works correctly.
     * It mocks the Firestore interactions and verifies that a user can be removed from a room successfully.
     * This test checks that the user's ID is cleared from the room.
     * This test also verifies that the Firestore methods are called as expected.
     */
    @Test
    void shouldRemoveUserFromRoomSuccessfully() throws Exception {
        // Arrange
        String roomId = "room123";
        String userId = "user456";

        Room room = new Room();
        room.setName("Test Room");
        room.setUserId(userId);
        room.setSensors(new ArrayList<>());

        List<Map<String, Object>> sensorsData = new ArrayList<>();
        Map<String, Object> sensorMap = new HashMap<>();
        sensorMap.put("id", "sensor1");
        sensorMap.put("sensorType", "DHT22");
        sensorMap.put("port", 2);
        sensorMap.put("details", new ArrayList<>());
        sensorsData.add(sensorMap);

        when(firestore.collection("rooms")).thenReturn(collectionReference);
        when(collectionReference.document(roomId)).thenReturn(documentReference);

        ApiFuture<DocumentSnapshot> snapshotFuture = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(snapshotFuture);
        when(snapshotFuture.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(Room.class)).thenReturn(room);
        when(snapshot.get("sensors")).thenReturn(sensorsData);

        // Mock update call
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);
        when(documentReference.update("userId","")).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        // Act
        RoomDTO result = roomServiceImpl.removeUserFromRoom(roomId, userId);

        // Assert
        assertNotNull(result);
        assertNotNull(result);
        assertEquals(roomId, result.getId());
        assertEquals("Test Room", result.getName());
        assertEquals("", result.getUserId());
        assertEquals(1, result.getSensors().size());
        assertEquals("sensor1", result.getSensors().get(0).getId());

        // Verify interactions
        verify(firestore).collection("rooms");
        verify(collectionReference).document(roomId);
        verify(documentReference).get();
        verify(documentReference).update("userId", "");
    }

}
