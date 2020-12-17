package com.uazbot.repository;

import com.uazbot.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findAllByText(String text);//просто правильное название метода даст возможность
    //избежать запросов на SQL

    @Query("select u from Test u where u.text like '%@gmail.com%'")//если этого мало можно написать
        //собственный запрос на языке похожем на SQL
    List<Test> findWhereEmailIsGmail();

    @Query(value = "select * from test where name like '%smith%'", nativeQuery = true)
        //если и этого мало - можно написать запрос на чистом SQL и все это будет работать
    List<Test> findWhereNameStartsFromSmith();
}
