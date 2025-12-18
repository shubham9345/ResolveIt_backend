package com.org.ResolveIt.service;

import com.org.ResolveIt.model.Comment;
import com.org.ResolveIt.model.Complaints;
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
        Comment comment = new Comment();
        comment.setDescription("Complaints submitted successfully");
        comment.setDate(LocalDate.now());
        commentRepository.save(comment);
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        complaints.setComments(commentList);
        if (complaints.isAnonymous()) {
            complaints.setUserId(null);
            complaintsRepository.save(complaints);
            return complaints;
        } else {

            complaintsRepository.save(complaints);
            return complaints;
        }
    }

    public List<Complaints> allComplaint() {
        return complaintsRepository.findAll();
    }

    public List<Complaints> allComplaintByUserId(Long userId) {
        List<Complaints> all_complaints = complaintsRepository.findAll();
        int n = all_complaints.size();
        List<Complaints> allComplaintByUserId = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (all_complaints.get(i).getUserId() == userId) {
                allComplaintByUserId.add(all_complaints.get(i));
            } else {
                continue;
            }
        }
        return allComplaintByUserId;
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
