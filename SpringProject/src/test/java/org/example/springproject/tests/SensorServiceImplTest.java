package org.example.springproject.tests;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.implementation.SensorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceImplTest {
    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @InjectMocks
    private SensorServiceImpl sensorServiceImpl;

    @Test
    void shouldAddSensorSuccessfully() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setPort(1);
        sensor.setSensorType("test");
        List<Details> detailsList = new ArrayList<>();
        sensor.setDetails(detailsList);

        // Mock Firestore references
        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);

        // Mock Firestore write result
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        when(documentReference.set(sensor)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(mock(WriteResult.class));

        // Act
        SensorDTO sensorDTO = sensorServiceImpl.addSensor(sensor);

        // Assert
        assertNotNull(sensorDTO);
        assertEquals("test", sensorDTO.getSensorType());
        assertEquals(1, sensorDTO.getPort());
        assertEquals(detailsList, sensorDTO.getDetails());
    }

    @Test
    void shouldDeleteSensorByIdSuccessfully() throws Exception {
        String sensorId = "sensor123";

        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.document(sensorId)).thenReturn(documentReference);

        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);

        when(documentReference.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getString("sensorType")).thenReturn("Temperature");
        when(snapshot.getLong("port")).thenReturn(1L);
        when(snapshot.get("details")).thenReturn(null);

        ApiFuture<WriteResult> deleteFuture = mock(ApiFuture.class);
        when(documentReference.delete()).thenReturn(deleteFuture);
        when(deleteFuture.get()).thenReturn(mock(WriteResult.class));

        SensorDTO result = sensorServiceImpl.deleteSensorById(sensorId);

        assertNotNull(result);
        assertEquals(sensorId, result.getId());
        assertEquals("Temperature", result.getSensorType());
        verify(documentReference).delete();
    }

    @Test
    void shouldUpdateSensorSuccessfully() throws Exception {
        String sensorId = "sensor123";
        Sensor updatedSensor = new Sensor("Humidity", 3, new ArrayList<>());

        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.document(sensorId)).thenReturn(documentReference);

        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);

        when(documentReference.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);

        ApiFuture<WriteResult> writeFuture = mock(ApiFuture.class);
        when(documentReference.set(updatedSensor)).thenReturn(writeFuture);
        when(writeFuture.get()).thenReturn(mock(WriteResult.class));

        SensorDTO result = sensorServiceImpl.updateSensor(sensorId, updatedSensor);

        assertNotNull(result);
        assertEquals("Humidity", result.getSensorType());
        assertEquals(3, result.getPort());
    }

    @Test
    void shouldReturnAllSensors() throws Exception {
        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        when(doc1.getId()).thenReturn("s1");
        when(doc1.getString("sensorType")).thenReturn("Temperature");
        when(doc1.getLong("port")).thenReturn(1L);
        when(doc1.get("details")).thenReturn(null);

        List<QueryDocumentSnapshot> docs = List.of(doc1);

        ApiFuture<QuerySnapshot> queryFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(docs);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(queryFuture);

        List<SensorDTO> sensors = sensorServiceImpl.getSensors();

        assertEquals(1, sensors.size());
        assertEquals("s1", sensors.get(0).getId());
    }

    @Test
    void shouldSaveSensorDataSuccessfully() throws Exception {
        String sensorId = "sensor123";

        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setId(sensorId);
        sensorDTO.setSensorType("Temperature");
        sensorDTO.setPort(1);

        Details detail = new Details();
        detail.setTimestamp(Timestamp.now());
        Map<String, Float> data = new HashMap<>();
        data.put("temperature", 25.5f);
        detail.setData(data);

        sensorDTO.setDetails(List.of(detail));

        DocumentReference documentReference = mock(DocumentReference.class);
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        WriteResult writeResult = mock(WriteResult.class);

        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.document(sensorId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(future);
        when(future.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false); // No existing data
        when(documentReference.set(any(SensorDTO.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);
        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        String result = sensorServiceImpl.saveSensorData(sensorDTO);

        assertNotNull(result);
    }

    @Test
    void shouldGetSensorByIdSuccessfully() throws Exception {
        String sensorId = "sensor123";

        DocumentReference documentReference = mock(DocumentReference.class);
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);

        Sensor sensor = new Sensor();
        sensor.setSensorType("Temperature");
        sensor.setPort(1);
        sensor.setDetails(new ArrayList<>());

        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.document(sensorId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(future);
        when(future.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.getString("sensorType")).thenReturn("Temperature");
        when(documentSnapshot.getLong("port")).thenReturn(1L);
        when(documentSnapshot.get("details")).thenReturn(new ArrayList<>());

        SensorDTO result = sensorServiceImpl.getSensorById(sensorId);

        assertNotNull(result);
        assertEquals("Temperature", result.getSensorType());
    }

    @Test
    void shouldGetSensorDataByDateSuccessfully() throws Exception {
        String sensorId = "sensor123";
        Date selectedDate = new Date();

        // Create matching detail
        Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(selectedDate.getTime() / 1000, 0);
        Map<String, Float> data = new HashMap<>();
        data.put("temperature", 25.5f);
        Details detail = new Details(timestamp, data);

        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setDetails(List.of(detail));

        DocumentReference documentReference = mock(DocumentReference.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);

        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.document(sensorId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(future);
        when(future.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(SensorDTO.class)).thenReturn(sensorDTO);

        List<Details> result = sensorServiceImpl.getSensorDataByDate(sensorId, selectedDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(data, result.get(0).getData());
    }

    @Test
    void shouldGetLastDetailForSensorSuccessfully() throws Exception {
        String sensorId = "sensor123";

        Timestamp timestamp1 = Timestamp.ofTimeSecondsAndNanos(1716720000, 0);
        Timestamp timestamp2 = Timestamp.ofTimeSecondsAndNanos(1716800000, 0);

        Map<String, Float> data1 = new HashMap<>();
        data1.put("temperature", 23.0f);
        Map<String, Float> data2 = new HashMap<>();
        data2.put("temperature", 26.0f);

        Details detail1 = new Details(timestamp1, data1);
        Details detail2 = new Details(timestamp2, data2);

        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setDetails(List.of(detail1, detail2));

        DocumentReference documentReference = mock(DocumentReference.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);

        when(firestore.collection("sensors")).thenReturn(collectionReference);
        when(collectionReference.document(sensorId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(future);
        when(future.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(SensorDTO.class)).thenReturn(sensorDTO);

        Details result = sensorServiceImpl.getLastDetailForSensor(sensorId);

        assertNotNull(result);
        assertEquals(timestamp2, result.getTimestamp());
        assertEquals(data2, result.getData());
    }

}
