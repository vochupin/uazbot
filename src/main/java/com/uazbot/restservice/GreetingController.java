package com.uazbot.restservice;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.uazbot.entity.Test;
import com.uazbot.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s! %s";
	private final AtomicLong counter = new AtomicLong();

	@Autowired
	TestService testService;

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		List<Test> tests = testService.list();

		return new Greeting(counter.incrementAndGet(), String.format(template, name, "тестовых записей = " + tests.size()));
	}
}
