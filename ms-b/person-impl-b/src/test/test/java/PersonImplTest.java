package com.llk.implB;

import com.llk.apiB.Person;
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

    @InjectMocks
    private PersonImpl personService;

    @Test
    void testGetById() {
        Map<String, String> data = new HashMap<>();
        data.put("id", "1");
        data.put("name", "A");
        data.put("age", "20");

        when(jedis.exists("person:1")).thenReturn(true);
        when(jedis.hgetAll("person:1")).thenReturn(data);

        Person p = personService.getById(1);
        assertNotNull(p);
        assertEquals("A", p.getName());

        when(jedis.exists("person:2")).thenReturn(false);
        assertNull(personService.getById(2));
    }

    @Test
    void testCreate() {
        Person p = new Person(1, "A", 20);
        when(jedis.exists("person:1")).thenReturn(false);

        personService.create(p);
        verify(jedis).hset(eq("person:1"), anyMap());

        when(jedis.exists("person:1")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> personService.create(p));
    }

    @Test
    void testUpdate() {
        Person p = new Person(1, "A", 20);
        when(jedis.exists("person:1")).thenReturn(true);

        personService.update(p);
        verify(jedis).hset(eq("person:1"), anyMap());

        when(jedis.exists("person:1")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> personService.update(p));
    }

    @Test
    void testDelete() {
        when(jedis.exists("person:1")).thenReturn(true);

        personService.delete(1);
        verify(jedis).del("person:1");

        when(jedis.exists("person:1")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> personService.delete(1));
    }
}
