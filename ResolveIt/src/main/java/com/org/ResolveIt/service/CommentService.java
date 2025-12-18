package com.org.ResolveIt.service;

import com.org.ResolveIt.Exception.UserNotFoundException;
import com.org.ResolveIt.model.Comment;
import com.org.ResolveIt.model.Complaints;
import com.org.ResolveIt.model.StatusType;
import com.org.ResolveIt.model.UserInfo;
import com.org.ResolveIt.repository.CommentRepository;
import com.org.ResolveIt.repository.ComplaintsRepository;
import com.org.ResolveIt.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ComplaintsRepository complaintsRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public String addCommentByComplaintId(Long complaintId, Long UserId, String desc, StatusType statusType) {
        Optional<Complaints> complaints = complaintsRepository.findById(complaintId);
        Optional<UserInfo> userInfo = userInfoRepository.findById(UserId);
        if (userInfo.isEmpty()) {
            throw new UserNotFoundException("user is not present with given userId", "user is not present with given userId");
        }
        if (userInfo.get().getRoles().equals("USER")) {
            throw new UserNotFoundException("you are not authorized to add comment", "you are not authority to complain");
        }
        if (complaints.isEmpty()) {
            throw new UserNotFoundException("complaint not found this complaintId", "complaint is not exist with this Id");
        } else {
            Comment comment = new Comment();
            comment.setComplaint(complaints.get());
            comment.setDate(LocalDate.now());
            comment.setUserId(UserId);
            comment.setDescription(desc);
            comment.setStatusType(statusType);
            complaints.get().getComments().add(comment);
            complaints.get().setStatusType(statusType);
            commentRepository.save(comment);
            complaintsRepository.save(complaints.get());
        }
        return "comment is successfully added to the complaintsId";
    }
}
