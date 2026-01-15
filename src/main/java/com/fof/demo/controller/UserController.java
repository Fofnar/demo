package com.fof.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fof.demo.model.User;
import com.fof.demo.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/search")
    public String getUser(@RequestParam String name, @RequestParam int age){
        return "Name: " + name + " Âge: " + age;
    }

    @GetMapping("/by-name/{name}/age/{age}")
    public String getUserWithpath(@PathVariable String name, @PathVariable int age){
        return "Nom (path) : " + name + ", Âge : " + age;
    }

    @PostMapping
    public User createUser(@RequestBody User user){
        System.out.println("Utilisateur reçu : " + user.getName() + ", âge : " + user.getAge());
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @GetMapping("/id/{id}")
    public User getUserById(@PathVariable Long id){
        return userRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user){
        return userRepository.findById(id)
                .map(existing ->{
                    existing.setName(user.getName());
                    existing.setAge(user.getAge());
                    return userRepository.save(existing);
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userRepository.deleteById(id);
    }
}
