package com.org.ResolveIt.service;

import com.org.ResolveIt.model.*;
import com.org.ResolveIt.repository.CommentRepository;
import com.org.ResolveIt.repository.ComplaintsRepository;
import com.org.ResolveIt.repository.UserInfoRepository;
import com.org.ResolveIt.utils.ConstantUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ComplaintsService {
    @Autowired
    private ComplaintsRepository complaintsRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private FileStorageService fileStorageService;

    public Complaints addComplaint(Complaints complaints) {

        complaints.setCreatedAt(LocalDate.now());

        if (complaints.getComments() == null) {
            complaints.setComments(new ArrayList<>());
        }
        Comment comment = new Comment();
        comment.setDescription("Complaint submitted successfully");
        comment.setStatusType(StatusType.valueOf("Open"));
        comment.setDate(LocalDate.now());
        comment.setComplaint(complaints);
        complaints.getComments().add(comment);

        return complaintsRepository.save(complaints);
    }

    public List<Complaints> allComplaint() {
        List<Complaints> complaintsList = complaintsRepository.findAll();
        Collections.reverse(complaintsList);
        return complaintsList;
    }

    public List<Complaints> allComplaintByUserId(Long userId) {
        return complaintsRepository.findByUserId(userId);
    }

    public List<Complaints> allAnonymousComplaints() {
        List<Complaints> all_complaints = complaintsRepository.findAll();
        int n = all_complaints.size();
        List<Complaints> allComplaint = new ArrayList<>();
        for (Complaints all_complaint : all_complaints) {
            if (all_complaint.getUserId() == null) {
                allComplaint.add(all_complaint);
            }
        }
        return allComplaint;
    }

    public List<Complaints> getCategoryType(CategoryType categoryType) {
        List<Complaints> allComplaints = complaintsRepository.findByCategoryType(categoryType);
        for (Complaints comp : allComplaints) {
            if (comp.getStatusType() == StatusType.Resolved) {
                continue;
            }
            if (comp.getStatusType() == StatusType.Escalated) {
                continue;
            }
            LocalDate createdDate = comp.getCreatedAt(); // assuming LocalDate
            long noOfDays = ChronoUnit.DAYS.between(createdDate, LocalDate.now());
            if (noOfDays >= 5) {
                comp.setStatusType(StatusType.Escalated);
            }
            complaintsRepository.save(comp);
        }
        return allComplaints;

    }

    public String complaintEscalation(Long complaintId, StatusType statusType) {

        Complaints complaint = complaintsRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));


        if (complaint.getStatusType() == StatusType.Resolved) {
            throw new IllegalStateException("Resolved complaint cannot be escalated");
        }


        LocalDate createdDate = complaint.getCreatedAt(); // assuming LocalDate
        long noOfDays = ChronoUnit.DAYS.between(createdDate, LocalDate.now());

        complaint.setStatusType(statusType);
        complaintsRepository.save(complaint);


        if (complaint.getUserId() != null) {
            UserInfo senderInfo = userInfoRepository.findById(complaint.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            emailService.sendComplaintStatusMail(
                    senderInfo.getEmail(),
                    complaint.getDescription(),
                    statusType
            );
        }

        return "Complaint escalated successfully!";
    }


    public List<Complaints> ComplaintsByFilter(
            LocalDate startDate,
            LocalDate endDate,
            StatusType statusType,
            CategoryType categoryType,
            UrgencyType urgencyType
    ) {
        List<Complaints> complaintsList = complaintsRepository.findAll();
        List<Complaints> result = new ArrayList<>();

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        for (Complaints comp : complaintsList) {

            LocalDate createdAt = comp.getCreatedAt();

            //  Date filter
            if (startDate != null &&
                    (createdAt.isBefore(startDate) || createdAt.isAfter(endDate))) {
                continue;
            }

            //  Category filter
            if (categoryType != null &&
                    comp.getCategoryType() != categoryType) {
                continue;
            }
            if (urgencyType != null &&
                    comp.getUrgencyType() != urgencyType) {
                continue;
            }

            //  Status filter
            if (statusType != null &&
                    comp.getStatusType() != statusType) {
                continue;
            }

            result.add(comp);
        }

        return result;
    }

    public List<Complaints> complaintsByFilterAssigned(
            LocalDate startDate,
            LocalDate endDate,
            StatusType statusType,
            CategoryType categoryType,
            UrgencyType urgencyType,
            boolean isAssigned
    ) {

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<Complaints> result = new ArrayList<>();
        List<Complaints> complaintsList = complaintsRepository.findAll();

        for (Complaints comp : complaintsList) {

            // Assigned / Non-assigned filter

            if (comp.isAssigned() != isAssigned) {
                continue;
            }

            LocalDate createdAt = comp.getCreatedAt();

            // Date filter
            if (startDate != null &&
                    (createdAt.isBefore(startDate) || createdAt.isAfter(endDate))) {
                continue;
            }

            // Category filter
            if (categoryType != null &&
                    comp.getCategoryType() != categoryType) {
                continue;
            }

            // Status filter
            if (statusType != null &&
                    comp.getStatusType() != statusType) {
                continue;
            }

            if (urgencyType != null &&
                    comp.getUrgencyType() != urgencyType) {
                continue;
            }

            result.add(comp);
        }

        return result;
    }


    public void exportToCsv(
            HttpServletResponse response,
            LocalDate startDate,
            LocalDate endDate,
            StatusType statusType,
            CategoryType categoryType,
            UrgencyType urgencyType
    ) throws IOException {

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=complaints.csv"
        );

        List<Complaints> complaints =
                ComplaintsByFilter(startDate, endDate, statusType, categoryType, urgencyType);

        PrintWriter writer = response.getWriter();

        // ===== REPORT TITLE =====
        writer.println("Complaints Report");
        writer.println();

        // ===== SUMMARY: CATEGORY =====
        writer.println("Summary - Complaints by Category");
        writer.println("Category,Count");

        Map<CategoryType, Long> categoryCount =
                complaints.stream()
                        .collect(Collectors.groupingBy(
                                Complaints::getCategoryType,
                                Collectors.counting()
                        ));

        for (Map.Entry<CategoryType, Long> entry : categoryCount.entrySet()) {
            writer.println(entry.getKey() + "," + entry.getValue());
        }

        writer.println();

        // ===== SUMMARY: URGENCY =====
        writer.println("Summary - Complaints by Urgency");
        writer.println("Urgency,Count");

        Map<UrgencyType, Long> urgencyCount =
                complaints.stream()
                        .collect(Collectors.groupingBy(
                                Complaints::getUrgencyType,
                                Collectors.counting()
                        ));

        for (Map.Entry<UrgencyType, Long> entry : urgencyCount.entrySet()) {
            writer.println(entry.getKey() + "," + entry.getValue());
        }

        writer.println();

        // ===== SUMMARY: STATUS =====
        writer.println("Summary - Complaints by Status");
        writer.println("Status,Count");

        Map<StatusType, Long> statusCount =
                complaints.stream()
                        .collect(Collectors.groupingBy(
                                Complaints::getStatusType,
                                Collectors.counting()
                        ));

        for (Map.Entry<StatusType, Long> entry : statusCount.entrySet()) {
            writer.println(entry.getKey() + "," + entry.getValue());
        }

        writer.println();

        // ===== COMPLAINTS TABLE =====
        writer.println("Complaints List");
        writer.println("ComplaintId,Category,Status,Description,Urgency,CreatedAt,UserId");

        for (Complaints c : complaints) {
            writer.println(
                    c.getComplaintsId() + "," +
                            c.getCategoryType() + "," +
                            c.getStatusType() + "," +
                            "\"" + c.getDescription().replace("\"", "\"\"") + "\"," +
                            c.getUrgencyType() + "," +
                            c.getCreatedAt() + "," +
                            c.getUserId()
            );
        }

        writer.flush();
        writer.close();
    }


    public void exportToPdf(
            HttpServletResponse response,
            LocalDate startDate,
            LocalDate endDate,
            StatusType statusType,
            CategoryType categoryType,
            UrgencyType urgencyType
    ) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=complaints_report.pdf"
        );

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // ================= TITLE =================
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Complaints Analytics Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // ================= FETCH DATA =================
        List<Complaints> complaints =
                ComplaintsByFilter(startDate, endDate, statusType, categoryType, urgencyType);

        // ================= STATISTICS =================
        Map<CategoryType, Long> categoryStats =
                complaints.stream().collect(Collectors.groupingBy(
                        Complaints::getCategoryType, Collectors.counting()
                ));

        Map<UrgencyType, Long> urgencyStats =
                complaints.stream().collect(Collectors.groupingBy(
                        Complaints::getUrgencyType, Collectors.counting()
                ));

        Map<StatusType, Long> statusStats =
                complaints.stream().collect(Collectors.groupingBy(
                        Complaints::getStatusType, Collectors.counting()
                ));

        // ================= STAT TABLE =================
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        document.add(new Paragraph("Complaint Statistics", sectionFont));
        document.add(new Paragraph(" "));

        PdfPTable statsTable = new PdfPTable(3);
        statsTable.setWidthPercentage(100);
        statsTable.addCell("Type");
        statsTable.addCell("Value");
        statsTable.addCell("Count");

        categoryStats.forEach((k, v) -> {
            statsTable.addCell("Category");
            statsTable.addCell(k.name());
            statsTable.addCell(v.toString());
        });

        urgencyStats.forEach((k, v) -> {
            statsTable.addCell("Urgency");
            statsTable.addCell(k.name());
            statsTable.addCell(v.toString());
        });

        statusStats.forEach((k, v) -> {
            statsTable.addCell("Status");
            statsTable.addCell(k.name());
            statsTable.addCell(v.toString());
        });

        document.add(statsTable);
        document.add(new Paragraph(" "));

        // ================= CHARTS =================
        document.add(new Paragraph("Visual Analytics", sectionFont));
        document.add(new Paragraph(" "));

        Image categoryPie = Image.getInstance(
                ConstantUtil.createPieChart("Complaints by Category", categoryStats)
        );
        categoryPie.scaleToFit(400, 300);
        document.add(categoryPie);

        Image statusBar = Image.getInstance(
                ConstantUtil.createBarChart("Complaints by Status", statusStats)
        );
        statusBar.scaleToFit(400, 300);
        document.add(statusBar);

        // ================= COMPLAINT TABLE =================
        document.add(new Paragraph("Complaint Details", sectionFont));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        table.addCell("ID");
        table.addCell("Category");
        table.addCell("Status");
        table.addCell("Description");
        table.addCell("Urgency");
        table.addCell("Created At");

        for (Complaints c : complaints) {
            table.addCell(String.valueOf(c.getComplaintsId()));
            table.addCell(c.getCategoryType().name());
            table.addCell(c.getStatusType().name());
            table.addCell(c.getDescription());
            table.addCell(c.getUrgencyType().name());
            table.addCell(c.getCreatedAt().toString());
        }

        document.add(table);
        document.close();
    }

}
