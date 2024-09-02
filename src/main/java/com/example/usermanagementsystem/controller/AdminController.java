package com.example.usermanagementsystem.controller;

import com.example.usermanagementsystem.dto.LoginRequest;
import com.example.usermanagementsystem.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) {
        return adminService.authenticateAdmin(loginRequest);
    }
}
