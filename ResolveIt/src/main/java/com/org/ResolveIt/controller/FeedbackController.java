package com.org.ResolveIt.controller;

import com.org.ResolveIt.model.Feedback;
import com.org.ResolveIt.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/{userId}")
    public Feedback addFeedbackByUserId(@PathVariable Long userId, @RequestBody Feedback feedback){
        return feedbackService.addFeedbackByUserId(feedback,userId);
    }
    @GetMapping("/all-feedback")
    public List<Feedback> feedbacks(){
        return feedbackService.feedbackList();
    }
}
