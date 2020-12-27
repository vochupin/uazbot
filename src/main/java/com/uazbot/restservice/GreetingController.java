package com.uazbot.restservice;

import com.uazbot.entity.Person;
import com.uazbot.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s! %s";
	private final AtomicLong counter = new AtomicLong();

	@Autowired
	PersonService personService;

	@GetMapping("/persons")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		List<Person> personList = personService.list();

		return new Greeting(counter.incrementAndGet(), String.format(template, name, "Записей по персонам = " + personList.size()));
	}
}
