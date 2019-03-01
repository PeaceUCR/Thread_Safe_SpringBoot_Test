package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.entity.Item;
import com.example.demo.repo.ItemRepo;
import com.example.demo.service.ItemService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger logger = LogManager.getLogger(DemoApplication.class);

    @Autowired
    ItemRepo itemRepo;


    @Autowired
    ItemService service;

    StringBuilder stringBuilder;
    Thread thread;

    private static final Object lock = new Object();
    @RequestMapping(value = "/test2", method = RequestMethod.GET)
    public String test2(){
        synchronized (lock){
                stringBuilder = new StringBuilder();
                thread = Thread.currentThread();
                stringBuilder.append(thread.getId());
                stringBuilder.append(thread.getName());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info(Thread.currentThread().getId()+Thread.currentThread().getName());
                return stringBuilder.toString();
        }
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        logger.info("Test STARTED:"+new Date());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Test COMPLETED:"+new Date());
        return new Date().toString();
    }

    @Transactional
    @RequestMapping(value = "/test3", method = RequestMethod.GET)
    public void test3(){
        Item item = itemRepo.findItemWithMaxID();
        //item.setId(1);
        System.out.println(item);
        itemRepo.insert(item.getId()+1, item.getDateCreated(),item.getLastUpdated(),item.getVersion(),item.getDetails(),item.getName());
        itemRepo.insert(item.getId(), item.getDateCreated(),item.getLastUpdated(),item.getVersion(),item.getDetails(),item.getName());
    }


    @RequestMapping(value = "/item/{id}", method = RequestMethod.GET)
    public Item getById(@PathVariable(value="id") Integer id){
        logger.info("Get by id:"+id);
        return service.getById(id);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Item> getAll(){
        logger.info("Get by all");
        return service.getAll();
    }

        /*
    * json body
    * {
	"name": "item2",
	"details": "This is item2"
    }
    * */

    @RequestMapping(value = "/create", method = RequestMethod.POST,consumes = "application/json; charset=utf-8")
    public void create(@RequestBody Item item){
        logger.info("Create Received Item"+ item);
        service.create(item);
    }

    /*
    * json body
    * {
	"id": 2,
	"name": "item2",
	"details": "This is item2 updated"
    }
    * */

    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json; charset=utf-8")
    public void update(@RequestBody String jsonData){
        logger.info("Update Received data"+ jsonData);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(jsonData).getAsJsonObject();

        Integer id = Integer.parseInt(obj.get("id").getAsString());
        String name =  obj.get("name").getAsString();
        String details =  obj.get("details").getAsString();
        System.out.println(jsonData);
        //service.updateDetailsByName(name, details);
        service.update(id, new Item(name, details));
    }

    /*
   * json body
   * {
   "id": 2
   }
   * */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, consumes = "application/json; charset=utf-8")
    public void delete(@RequestBody String jsonData){
        logger.info("Delete Received data"+jsonData);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(jsonData).getAsJsonObject();

        Integer id = Integer.parseInt(obj.get("id").getAsString());

        service.deleteById(id);
    }
}
