package com.llk.impl;
import com.llk.api.Person;
import com.llk.impl.kafka.PersonKafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonImplTest {
    @Mock
    private Jedis jedis;

    @Mock
    private PersonKafkaProducer kafkaProducer;

    @InjectMocks
    private PersonImpl personService;

    @Test
    void testGetById_Success() {
        int id = 1;
        String redisKey = "person:1";
        Map<String, String> personData = new HashMap<>();
        personData.put("id", "1");
        personData.put("name", "Lam Luong");
        personData.put("age", "33");

        when(jedis.exists(redisKey)).thenReturn(true);
        when(jedis.hgetAll(redisKey)).thenReturn(personData);

        Person result = personService.getById(id);

        assertNotNull(result);
        assertEquals("Lam Luong", result.getName());
        verify(jedis).hgetAll(redisKey);
    }

    @Test
    void testCreate_CallsKafka() {
        Person p = new Person(1, "Nhan", 30);
        personService.create(p);
        verify(kafkaProducer).sendCreate(p);
    }

    @Test
    void testUpdate_CallsKafka() {
        Person p = new Person(1, "Nhan Update", 31);
        personService.update(p);
        verify(kafkaProducer).sendUpdate(p);
    }

    @Test
    void testDelete_CallsKafka() {
        int idToDelete = 10;
        personService.delete(idToDelete);

        verify(kafkaProducer).sendDelete(argThat(person -> person.getId() == idToDelete));
    }
}
