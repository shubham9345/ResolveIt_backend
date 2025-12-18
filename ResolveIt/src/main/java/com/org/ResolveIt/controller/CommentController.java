package com.org.ResolveIt.controller;

import com.org.ResolveIt.model.Comment;
import com.org.ResolveIt.model.StatusType;
import com.org.ResolveIt.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {
    @Autowired
    private CommentService commentService;
    @PostMapping("/add-comment/{complaintsId}/{userId}")
    public String addCommentByComplaintId(@PathVariable Long complaintsId, @PathVariable Long userId, @RequestParam String desc, @RequestParam StatusType statusType){
        return commentService.addCommentByComplaintId(complaintsId, userId,desc,statusType);
    }
}
