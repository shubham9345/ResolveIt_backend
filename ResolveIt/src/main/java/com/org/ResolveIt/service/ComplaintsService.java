package com.org.ResolveIt.service;

import com.org.ResolveIt.Exception.UserNotFoundException;
import com.org.ResolveIt.model.*;
import com.org.ResolveIt.repository.CommentRepository;
import com.org.ResolveIt.repository.ComplaintsRepository;
import com.org.ResolveIt.repository.UserInfoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        if (complaints.isAnonymous()) {
            complaints.setUserId(null);
        }

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
        return complaintsRepository.findByCategoryType(categoryType);
    }

    public String complaintEscalation(Long complaintId, StatusType statusType) {

        Complaints complaint = complaintsRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));


        if (complaint.getStatusType() == StatusType.Resolved) {
            throw new IllegalStateException("Resolved complaint cannot be escalated");
        }


        LocalDate createdDate = complaint.getCreatedAt(); // assuming LocalDate
        long noOfDays = ChronoUnit.DAYS.between(createdDate, LocalDate.now());

        if (noOfDays < 7) {
            throw new IllegalStateException(
                    "Complaint must be at least 7 days old for escalation"
            );
        }

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
            CategoryType categoryType
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

            //  Status filter
            if (statusType != null &&
                    comp.getStatusType() != statusType) {
                continue;
            }

            result.add(comp);
        }

        return result;
    }

    public void exportToCsv(HttpServletResponse response,LocalDate startDate,LocalDate endDate,StatusType statusType,CategoryType categoryType) throws IOException {

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=complaints.csv"
        );

        List<Complaints> complaints = ComplaintsByFilter(startDate,endDate,statusType,categoryType);

        PrintWriter writer = response.getWriter();

        // CSV HEADER
        writer.println("ComplaintId,Category,Status,Description,Urgency,CreatedAt,UserId");

        // CSV DATA
        for (Complaints c : complaints) {
            writer.println(
                    c.getComplaintsId() + "," +
                            c.getCategoryType() + "," +
                            c.getStatusType() + "," +
                            "\"" + c.getDescription() + "\"," +
                            c.getUrgencyType() + "," +
                            c.getCreatedAt() + "," +
                            c.getUserId()

            );
        }

        writer.flush();
        writer.close();
    }
    public void exportToPdf(HttpServletResponse response,LocalDate startDate,LocalDate endDate,StatusType statusType,CategoryType categoryType) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=complaints.pdf"
        );

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Complaints Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        table.addCell("ID");
        table.addCell("Category");
        table.addCell("Status");
        table.addCell("Description");
        table.addCell("Urgency");
        table.addCell("Created At");

        List<Complaints> complaints = ComplaintsByFilter(startDate,endDate,statusType,categoryType);

        for (Complaints c : complaints) {
            table.addCell(String.valueOf(c.getComplaintsId()));
            table.addCell(String.valueOf(c.getCategoryType()));
            table.addCell(String.valueOf(c.getStatusType()));
            table.addCell(c.getDescription());
            table.addCell(String.valueOf(c.getUrgencyType()));
            table.addCell(String.valueOf(c.getCreatedAt()));
        }

        document.add(table);
        document.close();
    }
}
