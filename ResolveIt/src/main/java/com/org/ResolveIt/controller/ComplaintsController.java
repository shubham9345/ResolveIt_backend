package com.org.ResolveIt.controller;

import com.org.ResolveIt.model.*;
import com.org.ResolveIt.service.ComplaintsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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

    @GetMapping("/complaint-category")
    public List<Complaints> allEmployeeByCategoryType(@RequestParam CategoryType categoryType) {
        return complaintsService.getCategoryType(categoryType);
    }

    @PostMapping("/escalate-complaint/{complaintId}")
    public String escalationComplaint(@PathVariable Long complaintId, @RequestParam StatusType statusType) {
        return complaintsService.complaintEscalation(complaintId, statusType);
    }

    @GetMapping("/filter-complaints")
    public List<Complaints> ComplaintsFilter(@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate, @RequestParam(required = false) CategoryType categoryType, @RequestParam(required = false) StatusType statusType,@RequestParam(required = false) UrgencyType urgencyType) {
        return complaintsService.ComplaintsByFilter(startDate, endDate, statusType, categoryType,urgencyType);
    }

    @GetMapping("/filter-complaints-assigned")
    public List<Complaints> ComplaintsFilterByAssigned(@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate, @RequestParam(required = false) CategoryType categoryType, @RequestParam(required = false) StatusType statusType, @RequestParam(required = false) UrgencyType urgencyType,@RequestParam(required = false) boolean isAssigned) {
        return complaintsService.complaintsByFilterAssigned(startDate, endDate, statusType, categoryType,urgencyType,isAssigned);
    }

    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response, @RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate, @RequestParam(required = false) CategoryType categoryType, @RequestParam(required = false) StatusType statusType, @RequestParam String exportType,@RequestParam(required = false) UrgencyType urgencyType) throws Exception {
        if (exportType.equals("csv")) {
            complaintsService.exportToCsv(response, startDate, endDate, statusType, categoryType,urgencyType);
        } else{
            complaintsService.exportToPdf(response,startDate,endDate,statusType,categoryType,urgencyType);
        }
    }
}

