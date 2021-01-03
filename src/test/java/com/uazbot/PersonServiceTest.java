package com.uazbot;

import com.uazbot.entity.Person;
import com.uazbot.repository.PersonRepository;
import com.uazbot.service.PersonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PersonServiceTest {

    private static final int PERSONS_NUMBER = 8;
    @Autowired
    AppConfig appConfig;

    @Autowired
    PersonService personService;

    @Autowired
    PersonRepository personRepository;

    private List<String> placeNames= new ArrayList<String>() {{
        add("Томская область, Сибирский федеральный округ, Россия");
        add("городской округ Новосибирск, Новосибирская область, Сибирский федеральный округ, Россия");
        add("городской округ Омск, Омская область, Сибирский федеральный округ, Россия");
        add("Екатеринбург, городской округ Екатеринбург, Свердловская область, Уральский федеральный округ, Россия");
        add("Воронеж, городской округ Воронеж, Воронежская область, Центральный федеральный округ, Россия");
        add("Москва, Центральный федеральный округ, Россия");
        add("Київ, Київська міська громада, Україна");
        add("London, Greater London, England, United Kingdom");
    }};

    private List<Coordinate> coordinates =  new ArrayList<Coordinate>() {{
        add(new Coordinate(82.0475315, 58.6124279));
        add(new Coordinate(82.9234509, 55.0282171));
        add(new Coordinate(73.371529, 54.991375));
        add(new Coordinate(60.60825, 56.839104));
        add(new Coordinate(39.2005858, 51.6605982));
        add(new Coordinate(37.6174943, 55.7504461));
        add(new Coordinate(30.5241361, 50.4500336));
        add(new Coordinate(-0.1276474, 51.5073219));
    }};

    @Before
    public void initData() {
        personRepository.deleteAll();

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        for (int i = 0; i < PERSONS_NUMBER; i++) {
            Person person = new Person();
            person.setPid(278123450L + i);
            person.setFirstName("Василий" + i);
            person.setLastName("Пупкин" + i);
            person.setUserName("pupkin" + i);
            person.setUserPlace(placeNames.get(i));

            generateTextField(person);

            person.setOsmMapPoint(geometryFactory.createPoint(coordinates.get(i)));

            personService.createOrUpdatePerson(person);
        }
    }

    @Test
    public void searchByRangeFromTsk() {
        List<Person> personList = personService.listByRange(278123450L, null);

        Long[] fromTskPids = new Long[]{278123450L, 278123451L, 278123452L, 278123453L, 278123454L, 278123455L, 278123456L, 278123457L};

        for (int i = 0; i < PERSONS_NUMBER; i++) {
            assertEquals(fromTskPids[i], personList.get(i).getPid());
        }
    }

    @Test
    public void searchByRangeFromLondon() {
        List<Person> personList = personService.listByRange(278123457L, null);

        Long[] fromTskPids = new Long[]{278123457L, 278123456L, 278123455L, 278123454L, 278123453L, 278123452L, 278123450L, 278123451L};

        for (int i = 0; i < PERSONS_NUMBER; i++) {
            assertEquals(fromTskPids[i], personList.get(i).getPid());
        }
    }

    @Test
    public void limitedSearchByRange() {
        List<Person> personList = personService.listByRange(278123457L, 5);

        assertEquals(personList.size(), 5);

        Long[] fromTskPids = new Long[]{278123457L, 278123456L, 278123455L, 278123454L, 278123453L};

        for (int i = 0; i < 5; i++) {
            assertEquals(fromTskPids[i], personList.get(i).getPid());
        }
    }

    private void generateTextField(Person person) {
        person.setText(Stream.of(person.getFirstName(), person.getLastName(), person.getUserName())
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.joining(" ")));
    }
}