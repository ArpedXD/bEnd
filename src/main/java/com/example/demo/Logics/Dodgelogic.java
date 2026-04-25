package com.example.demo.Logics;

import com.example.demo.database.databases;
import com.example.demo.base.base;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class Dodgelogic {
    public void Score(String input, String b){
        if(Objects.equals(input, "win")) {
            databases.win(b,"Dodge");
        } else if (Objects.equals(input,"lose")) {
            databases.lose(b,"Dodge");
        }
    }
}
