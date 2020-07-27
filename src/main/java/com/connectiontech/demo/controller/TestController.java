package com.connectiontech.demo.controller;

import com.connectiontech.demo.annotation.RSASign;
import com.connectiontech.demo.entity.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {


    @RequestMapping("/rSASign")
    @RSASign
    public R do3( @RequestParam Map<String, String> map) {
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            System.out.println(stringStringEntry.getKey()+":"+stringStringEntry.getValue());
        }
        return R.ok("hello");
    }
}
