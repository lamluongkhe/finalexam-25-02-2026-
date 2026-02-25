package com.llk.rest;
import com.llk.api.Person;
import com.llk.api.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonRestTest {
    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonRest personRest;

    @Test
    void testGet() {
        int id = 1;
        Person person = new Person(id, "Lam Luong", 33);
        when(personService.getById(id)).thenReturn(person);

        Response response = personRest.get(id);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(person, response.getEntity());
        verify(personService).getById(id);
    }

    @Test
    void testCreate() {
        Person person = new Person(1, "New Person", 20);
        Response response = personRest.create(person);

        // N?u code th?c t? tr? v? OK (200) thay vì CREATED (201), hãy s?a dòng du?i thành .OK
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(personService).create(person);
    }

    @Test
    void testUpdate() {
        Person person = new Person(1, "Update Person", 25);
        Response response = personRest.update(person);

        // Thông thu?ng Update tr? v? OK (200) ho?c ACCEPTED (202)
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(personService).update(person);
    }

    @Test
    void testDelete() {
        int id = 10;
        Response response = personRest.delete(id);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(personService).delete(id);
    }
}
