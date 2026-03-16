package com.fof.demo.controller;

import com.fof.demo.dto.AdminStatsDTO;
import com.fof.demo.dto.ApiResponse;
import com.fof.demo.service.AdminService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ApiResponse<AdminStatsDTO> stats(){
        AdminStatsDTO stats = adminService.getStats();
        return new ApiResponse<>(
                true,
                stats,
                "Statistics retrieved successfully",
                LocalDateTime.now()
        );
    }

}