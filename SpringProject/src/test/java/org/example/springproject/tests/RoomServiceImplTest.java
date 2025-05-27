package org.example.springproject.tests;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.RoomDTO;
import org.example.springproject.entity.Room;
import org.example.springproject.service.implementation.RoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceImplTest {
    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;


    @Mock
    private DocumentReference documentReference;

    @InjectMocks
    private RoomServiceImpl roomServiceImpl;


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

}
