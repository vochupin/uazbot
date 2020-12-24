package com.uazbot.service;

import com.uazbot.entity.Person;
import com.uazbot.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    public void createPerson(Person person){
        personRepository.save(person);
    }

    public List<Person> list() {
        return personRepository.findAll();
    }
}
