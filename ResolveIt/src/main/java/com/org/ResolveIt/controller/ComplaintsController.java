package com.org.ResolveIt.controller;

import com.org.ResolveIt.model.Complaints;
import com.org.ResolveIt.service.ComplaintsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaint")
@CrossOrigin
public class ComplaintsController {
    @Autowired
    private ComplaintsService complaintsService;

    @PostMapping("/add-complaint")
    public ResponseEntity<Complaints> addComplaints(@RequestBody Complaints complaints) {
        Complaints complaint = complaintsService.addComplaint(complaints);
        return new ResponseEntity<>(complaint, HttpStatus.OK);
    }

    @GetMapping("/all-complaints")
    public List<Complaints> allComplaints() {
        return complaintsService.allComplaint();
    }

    @GetMapping("/all-complaints/{userId}")
    public List<Complaints> allComplaintsByUserId(@PathVariable Long userId) {
        return complaintsService.allComplaintByUserId(userId);
    }

    @GetMapping("/all-anonymous")
    public List<Complaints> allanonymousComplaints() {
        return complaintsService.allAnonymousComplaints();
    }
}
