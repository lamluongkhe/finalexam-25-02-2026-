package com.llk.implB.kafka;

import com.google.gson.Gson;
import com.llk.apiB.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonKafkaConsumerTest {
    @Mock
    private PersonService personService;

    @Mock
    private KafkaConsumer<String, String> consumer;

    @InjectMocks
    private PersonKafkaConsumer personKafkaConsumer;

    @Test
    void testRun() {
        Gson gson = new Gson();
        String json = gson.toJson(new PersonEvent(PersonEventType.CREATE, new Person(1, "A", 10)));

        TopicPartition tp = new TopicPartition("person-events", 0);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("person-events", 0, 0, "1", json);

        ConsumerRecords<String, String> records = new ConsumerRecords<>(
                Collections.singletonMap(tp, Collections.singletonList(record))
        );

        when(consumer.poll(any(Duration.class)))
                .thenReturn(records)
                .thenThrow(new WakeupException());

        personKafkaConsumer.run();

        verify(personService).create(any());
        verify(consumer).close();
    }

    @Test
    void testStop() {
        personKafkaConsumer.stop();
        verify(consumer).wakeup();
    }
}
