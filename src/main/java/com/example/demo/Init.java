package com.example.demo;

import com.example.demo.entity.Item;
import com.example.demo.repo.ItemRepo;
import com.example.demo.service.ItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class Init implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    ItemService service;

    //only direct use repo do the test the thread safe of JPARepository
    @Autowired
    ItemRepo itemRepo;

    private static final Logger logger = LogManager.getLogger(DemoApplication.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        Item item1 = new Item("item1", "this is item1");
        Item item2 = new Item("item2", "this is item2");
        Item item3 = new Item("item3", "this is item3");
        System.out.println(itemRepo.save(item1));
        System.out.println(itemRepo.save(item2));
        System.out.println(itemRepo.save(item3));



        //System.out.println(itemRepo.findItemWithMaxID());

        /*
        for(int i =0; i<10;i++){
            //Error:(36, 45) java: local variables referenced from a lambda expression must be final or effectively final
            final Item item = new Item("item"+i, "this is item"+i);
            new Thread(()->{
                //synchronized (itemRepo){
                    System.out.println(itemRepo.save(item));
                    System.out.println(itemRepo.findItemWithMaxID());
                //}
            }).start();
        }
        */
    }
}
