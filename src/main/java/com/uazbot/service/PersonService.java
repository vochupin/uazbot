package com.uazbot.service;

import com.uazbot.entity.Person;
import com.uazbot.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Person> findByName(String name) {
        return personRepository.findByName(name);
    }

    public List<Person> findByAddress(String address) {
        return personRepository.findByAddress(address);
    }

    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }
}
