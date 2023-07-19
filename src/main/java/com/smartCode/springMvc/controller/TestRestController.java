package com.smartCode.springMvc.controller;

import com.smartCode.springMvc.dto.UserFilter;
import com.smartCode.springMvc.model.User;
import com.smartCode.springMvc.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller
//@ResponseBody
@RestController
public class TestRestController {

    @Autowired
    public UserService userService;

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        var user = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    @PostMapping(value = "/users")
    public ResponseEntity<User> create(@RequestBody User user) {
        User register = userService.register(user);
        return ResponseEntity.status(HttpStatus.OK).body(register);
    }

    @PutMapping(value = "/users/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        User register = userService.update(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(register);
    }

    @PatchMapping(value = "/users/{id}")
    public ResponseEntity<User> updatePartially(@PathVariable Long id, @RequestBody User user) {
        User register = userService.updatePartially(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(register);
    }

    @PostMapping("/users/filter")
    public ResponseEntity<List<User>> filter(@RequestBody UserFilter userFilter) {

        List<User> users = userService.filter(userFilter);
        return ResponseEntity.ok(users);
    }


    @PostMapping(value = "/users/old")
    public ResponseEntity<User> createOld(@RequestParam String name,
                                          @RequestParam String lastname,
                                          @RequestParam Double balance,
                                          @RequestParam String email,
                                          @RequestParam String password,
                                          @RequestParam int age) {
        User user = new User();
        user.setName(name);
        user.setLastname(lastname);
        user.setAge(age);
        user.setEmail(email);
        user.setBalance(balance);
        user.setPassword(password);
        User register = userService.register(user);
        return ResponseEntity.status(HttpStatus.OK).body(register);
    }

}
