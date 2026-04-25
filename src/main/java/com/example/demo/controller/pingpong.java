package com.example.demo.controller;
import com.example.demo.Logics.pingponglogic;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pingpong")
@CrossOrigin(origins = "*")
public class pingpong {

    private final pingponglogic service;

    public pingpong(pingponglogic service) {
        this.service = service;
    }

    @PostMapping("/end")
    public void Score(@RequestBody ScoreRequest inp){
        service.Score(inp.a, inp.b);
    }
}

