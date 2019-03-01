package com.example.demo.repo;

import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ItemRepo extends CrudRepository<Item, Integer> {
   //return item
   //@Transactional
   public Item save(Item item);

   public List<Item> findAll();

   public Item findByName(String name);

   //https://stackoverflow.com/questions/7604893/sql-select-row-from-table-where-id-maxid
   //https://www.baeldung.com/spring-data-jpa-query
   @Query(value = "select * from ITEM i where i.id = (select max(id) from ITEM)", nativeQuery = true)
   public Item findItemWithMaxID();

   @Transactional
   @Modifying
   @Query(value = "insert into ITEM values (:id, :dateCreated, :lastUpdated, :version, :details, :name)", nativeQuery = true)
   public void insert(@Param("id")Integer id,@Param("dateCreated")Date dateCreated, @Param("lastUpdated")Date lastUpdated, @Param("version")Integer version, @Param("details")String details,  @Param("name")String name);

   //update
   //https://stackoverflow.com/questions/29601028/transaction-required-exception-jpa-spring
   @Transactional
   @Modifying
   @Query("update Item i set i.details = :newdetails where i.name = :name")
   //java.lang.IllegalArgumentException: Modifying queries can only use void or int/Integer as return type!
   public int updateDetailsByName(@Param("name") String name, @Param("newdetails") String newdetails);
}
