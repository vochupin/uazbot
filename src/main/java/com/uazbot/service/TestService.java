package com.uazbot.service;

import com.uazbot.entity.Test;
import com.uazbot.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    @Autowired
    TestRepository testRepository;

    public void createTest(Test test){
        testRepository.save(test);
    }

    public List<Test> list() {
        return testRepository.findAll();
    }
}
