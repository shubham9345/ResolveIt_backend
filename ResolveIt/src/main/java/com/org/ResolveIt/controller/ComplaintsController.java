package com.org.ResolveIt.controller;

import com.org.ResolveIt.model.CategoryType;
import com.org.ResolveIt.model.Complaints;
import com.org.ResolveIt.model.Employee;
import com.org.ResolveIt.model.StatusType;
import com.org.ResolveIt.service.ComplaintsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

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
    @Retryable(
            value = { RestClientException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<Complaints> allComplaintsByUserId(@PathVariable Long userId) {
        return complaintsService.allComplaintByUserId(userId);
    }

    @GetMapping("/all-anonymous")
    public List<Complaints> allanonymousComplaints() {
        return complaintsService.allAnonymousComplaints();
    }
    @GetMapping("/complain/{complainId}")

    public Complaints findcomplainBycomplainId(@PathVariable Long complainId){
        return complaintsService.findByComplaintd(complainId);
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
    public List<Complaints> ComplaintsFilter(@RequestParam(required = true) LocalDate startDate, @RequestParam(required = false) LocalDate endDate, @RequestParam(required = false) CategoryType categoryType, @RequestParam(required = false) StatusType statusType) {
        return complaintsService.ComplaintsByFilter(startDate, endDate, statusType, categoryType);
    }

    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response, @RequestParam(required = true) LocalDate startDate, @RequestParam(required = false) LocalDate endDate, @RequestParam(required = false) CategoryType categoryType, @RequestParam(required = false) StatusType statusType, @RequestParam String exportType) throws Exception {
        if (exportType.equals("csv")) {
            complaintsService.exportToCsv(response, startDate, endDate, statusType, categoryType);
        } else{
            complaintsService.exportToPdf(response,startDate,endDate,statusType,categoryType);
        }
    }
}

