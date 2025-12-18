package com.org.ResolveIt.service;

import com.org.ResolveIt.model.Comment;
import com.org.ResolveIt.model.Complaints;
import com.org.ResolveIt.model.StatusType;
import com.org.ResolveIt.repository.CommentRepository;
import com.org.ResolveIt.repository.ComplaintsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComplaintsService {
    @Autowired
    private ComplaintsRepository complaintsRepository;
    @Autowired
    private CommentRepository commentRepository;

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
        return complaintsRepository.findAll();
    }

    public List<Complaints> allComplaintByUserId(Long userId) {
        return complaintsRepository.findByUserId(userId);
    }

    public List<Complaints> allAnonymousComplaints() {
        List<Complaints> all_complaints = complaintsRepository.findAll();
        int n = all_complaints.size();
        List<Complaints> allComplaint = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (all_complaints.get(i).getUserId() == null) {
                allComplaint.add(all_complaints.get(i));
            } else {
                continue;
            }
        }
        return allComplaint;
    }

}
