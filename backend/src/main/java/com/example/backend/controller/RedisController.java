package com.example.backend.controller;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;



@RestController


public class RedisController {

    private StringRedisTemplate redis;
    RedisController(StringRedisTemplate redis){
        this.redis=redis;
    }


    @GetMapping("/user")
   public long setUser(){
   return  redis.getExpire("name");
     
    
    
 



    

   }
    
    
}
