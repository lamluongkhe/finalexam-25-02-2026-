package com.llk.impl.kafka;
import com.llk.api.Person;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonKafkaProducerTest {

    @Mock
    private KafkaProducer<String, String> producer;

    @InjectMocks
    private PersonKafkaProducer personKafkaProducer;

    @Test
    void testSendCreate_ThanhCong() {
        Person p = new Person(4, "Lam Luong", 33);

        personKafkaProducer.sendCreate(p);

        verify(producer, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendDelete_DungID_PhaiGuiMessage() {
        Person p = new Person(10, "Delete Test", 0);

        personKafkaProducer.sendDelete(p);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(producer).send(captor.capture());

        ProducerRecord<String, String> record = captor.getValue();
        assertEquals("person-events", record.topic());
        assertEquals("10", record.key());
        assertTrue(record.value().contains("\"type\":\"DELETE\""));
    }

    @Test
    void testSendDelete_SaiID_KhongDuocGuiMessage() {
        Person p = new Person(0, "Invalid", 0);

        personKafkaProducer.sendDelete(p);

        verify(producer, never()).send(any());
    }

    @Test
    void testSendUpdate_DungTopicVaKey() {
        Person p = new Person(1, "Update Name", 25);

        personKafkaProducer.sendUpdate(p);

        verify(producer).send(argThat(record ->
                record.topic().equals("person-events") &&
                        record.key().equals("1")
        ));
    }

    @Test
    void testDestroy_PhaiDongProducer() {
        personKafkaProducer.destroy();

        verify(producer, times(1)).close();
    }
}
