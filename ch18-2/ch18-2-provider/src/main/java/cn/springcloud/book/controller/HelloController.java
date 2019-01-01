package cn.springcloud.book.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xujin
 **/
@RestController
public class HelloController {

    @GetMapping("/customFilter")
    public String customFilter(@RequestParam String name) {
        return "customFilter, " + name + "!";
    }


    @PostMapping("/test/post")
    public  void getStrin(@RequestBody List<String> str){
        System.out.println(str.toString());
    }

    public static void main(String[] args) {
        List<String> list=new ArrayList<>();
        for(int i=1;i<100;i++) {
            list.add("assasasasasas");
        }
        System.out.println(JSON.toJSONString(list));
    }
}
