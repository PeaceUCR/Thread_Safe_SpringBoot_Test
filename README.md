#SpringBoot MultiThread Test
# Controller Thread Safe? NO!
What's thread safe?
https://stackoverflow.com/questions/9555842/why-servlets-are-not-thread-safe?noredirect=1&lq=1
        
        Servlets are normal java classes and thus are NOT Thread Safe.
        
        But that said, Java classes are Thread safe if you do not have instance variables. Only instance variables need to synchronize. (Instance variable are variables declared in the class and not in within its methods.）
        
        Variables declared in the methods are thread safe as each thread creates it own Program Stack and function variables are allocated in the stack. This means that variable in a methods are created for each thread, hence does not have any thread sync issues associated.
        
        Method variables are thread-safe, class variables are not.


**Test 1**:(Same Controller same Method/endpoint, two requests), controller code
        
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

http://localhost:8080/items/test
in postman & chrome_Incognito

Output      
        
        [INFO ] 2019-02-27 17:59:19.093 [http-nio-8080-exec-1] DemoApplication - Handler STARTED:Wed Feb 27 17:59:19 PST 2019
        [INFO ] 2019-02-27 17:59:21.283 [http-nio-8080-exec-3] DemoApplication - Handler STARTED:Wed Feb 27 17:59:21 PST 2019
        [INFO ] 2019-02-27 17:59:29.098 [http-nio-8080-exec-1] DemoApplication - Handler COMPLETED:Wed Feb 27 17:59:29 PST 2019
        [INFO ] 2019-02-27 17:59:31.287 [http-nio-8080-exec-3] DemoApplication - Handler COMPLETED:Wed Feb 27 17:59:31 PST 2019

one request method block will not affect receive, and we can see these two request method is in different thread        

**Test 2**:(Same Controller two Method/endpoint, four requests)  
http://localhost:8080/items/test
http://localhost:8080/items/test2

Output:   
        
        [INFO ] 2019-02-27 18:18:16.767 [http-nio-8080-exec-6] DemoApplication - Test2 STARTED:Wed Feb 27 18:18:16 PST 2019
        [INFO ] 2019-02-27 18:18:20.195 [http-nio-8080-exec-7] DemoApplication - Test STARTED:Wed Feb 27 18:18:20 PST 2019
        [INFO ] 2019-02-27 18:18:23.199 [http-nio-8080-exec-8] DemoApplication - Test2 STARTED:Wed Feb 27 18:18:23 PST 2019
        [INFO ] 2019-02-27 18:18:24.910 [http-nio-8080-exec-9] DemoApplication - Test STARTED:Wed Feb 27 18:18:24 PST 2019
        [INFO ] 2019-02-27 18:18:26.773 [http-nio-8080-exec-6] DemoApplication - Test2 COMPLETED:Wed Feb 27 18:18:26 PST 2019
        [INFO ] 2019-02-27 18:18:30.199 [http-nio-8080-exec-7] DemoApplication - Test COMPLETED:Wed Feb 27 18:18:30 PST 2019
        [INFO ] 2019-02-27 18:18:33.203 [http-nio-8080-exec-8] DemoApplication - Test2 COMPLETED:Wed Feb 27 18:18:33 PST 2019
        [INFO ] 2019-02-27 18:18:34.911 [http-nio-8080-exec-9] DemoApplication - Test COMPLETED:Wed Feb 27 18:18:34 PST 2019 
    
        
Conclusion we know the controller**method**is executed by different thread

# Thread Safe in Spring-Boot Controller
https://stackoverflow.com/questions/42091666/how-to-have-thread-safe-controller-in-spring-boot 
https://stackoverflow.com/questions/11508405/are-spring-mvc-controllers-singletons    

Controller is singleton by default,
Let's try output the current thread name in response 

**Test 3**    
code:       
        
            @RequestMapping(value = "/test2", method = RequestMethod.GET)
            public String test2(){
                StringBuilder stringBuilder = new StringBuilder();
                Thread thread = Thread.currentThread();
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

chrome first hit, postman second   
 
chrome: 45http-nio-8080-exec-3  

postman: 47http-nio-8080-exec-5

Console:
        
        [INFO ] 2019-02-27 19:09:15.682 [http-nio-8080-exec-3] DemoApplication - 45http-nio-8080-exec-3
        [INFO ] 2019-02-27 19:09:18.632 [http-nio-8080-exec-5] DemoApplication - 47http-nio-8080-exec-5

Then the app running correct, why?

Variables declared in the methods are thread safe as each thread creates it own Program Stack and function variables are allocated in the stack. This means that variable in a methods are created for each thread, hence does not have any thread sync issues associated.
refer to https://stackoverflow.com/questions/9555842/why-servlets-are-not-thread-safe?noredirect=1&lq=1
**Test 4**
change to stringBuilder, thread to  Instance variable(Instance variable are variables declared in the class and not in within its methods.）        
        
            StringBuilder stringBuilder;
            Thread thread;
        
        
            @RequestMapping(value = "/test2", method = RequestMethod.GET)
            public String test2(){
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
chrome first hit, postman second  

chrome:  43http-nio-8080-exec-5

postman: 43http-nio-8080-exec-5

Console:
        
        [INFO ] 2019-02-28 14:36:42.109 [http-nio-8080-exec-4] DemoApplication - 42http-nio-8080-exec-4
        [INFO ] 2019-02-28 14:36:44.478 [http-nio-8080-exec-5] DemoApplication - 43http-nio-8080-exec-5

It's wrong!

Conclusion: Class with Instance variable are not thread safe. once the thread one is sleep, the stringBuilder has been changed by thread two.


**Test 5**
How to solve the bug in test4, try lock(synchronized).

change1:
        
        public synchronized String test2()

then the output is correct, synchronized the method, only one thread at one time can access. But it's not reasonable in prod    
    
chrome:  38http-nio-8080-exec-1

postman: 40http-nio-8080-exec-3
        
        [INFO ] 2019-02-28 15:14:53.227 [http-nio-8080-exec-1] DemoApplication - 38http-nio-8080-exec-1
        [INFO ] 2019-02-28 15:15:03.232 [http-nio-8080-exec-3] DemoApplication - 40http-nio-8080-exec-3
          
change2: synchronized block
if do something like(this way not working!)
        
                synchronized (stringBuilder){
                    synchronized (thread){
                    }
                }
**java.lang.NullPointerException: null will report.**        

https://stackoverflow.com/questions/10195054/synchronized-object-set-to-null
You should never change the reference of the object you're synchronizing on, much less set it to null, which will cause a NullPointerException on any further attempts to synchronize on it.   

https://stackoverflow.com/questions/6433493/nullpointerexception-on-synchronized-statement
You should not be synchronizing on a reference that itself may be changed. If another thread is allowed to replace globalObj, that means you might hold a lock to the old globalObj while another thread works on an entirely different one - the lock doesn't help you at all.

**Try this(?????Why Lock unrelated object can achieve this???)Please Investigate more before try use in prod**
            
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

Output is correct 
                 
chrome:  38http-nio-8080-exec-1

postman: 40http-nio-8080-exec-3  
        
        [INFO ] 2019-02-28 15:48:09.044 [http-nio-8080-exec-1] DemoApplication - 38http-nio-8080-exec-1
        [INFO ] 2019-02-28 15:48:19.046 [http-nio-8080-exec-3] DemoApplication - 40http-nio-8080-exec-3 


# Service Thread Safe (Spring singleton beans NOT thread safe)   
Base on tests above, instance variable is **not** thread safe, then @Autowired itemservice is not as well

https://stackoverflow.com/questions/46702286/are-autowired-objects-in-spring-mvc-threadsafe
https://stackoverflow.com/questions/6419393/is-this-design-of-spring-singleton-beans-thread-safe/6419421#6419421

# JPA Repository Thread Safe? (Safe refer to below)     
https://stackoverflow.com/questions/15965735/is-a-spring-data-jpa-repository-thread-safe-aka-is-simplejparepository-threa     
https://stackoverflow.com/questions/37413707/regarding-spring-jparepository-method-thread-safety

        
        org.springframework.dao.InvalidDataAccessApiUsageException: Executing an update/delete query; nested exception is javax.persistence.TransactionRequiredException: Executing an update/delete query

More about @Transactional
https://stackoverflow.com/questions/1099025/spring-transactional-what-happens-in-background

        
        The simplest answer is, on whichever method you declare @Transactional the boundary of transaction starts and boundary ends when method completes.
        
        If you are using JPA call then all commits are with in this transaction boundary. Lets say you are saving entity1, entity2 and entity3. Now while saving entity3 an exception occur then as enitiy1 and entity2 comes in same transaction so entity1 and entity2 will be rollback with entity3.
        
        Transaction : (entity1.save, entity2.save, entity3.save). Any exception will result in rollback of all JPA transactions with DB. Internally JPA transaction are used by Spring.

Test: 
check db and then hit http://localhost:8080/items/test3

the first success, the second with exception
        
        itemRepo.insert(item.getId()+1, item.getDateCreated(),item.getLastUpdated(),item.getVersion(),item.getDetails(),item.getName());
        itemRepo.insert(item.getId(), item.getDateCreated(),item.getLastUpdated(),item.getVersion(),item.getDetails(),item.getName());

and DB data accept the first, Let's check add the @Transactional to controller method
then even the first success but the data not insert into. As expected in previous explanation 

            