// package com.cloudpulse.backend.controller;

// import com.cloudpulse.backend.entity.User;
// import com.cloudpulse.backend.service.UserService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/users")
// @CrossOrigin(origins = "http://localhost:4200")
// public class UserController {

//     @Autowired
//     private UserService service;

//     @PostMapping("/register")
//     public User register(@RequestBody User user) {
//         return service.registerUser(user);
//     }
// }