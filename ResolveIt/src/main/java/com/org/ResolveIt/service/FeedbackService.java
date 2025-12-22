package com.org.ResolveIt.service;

import com.org.ResolveIt.Exception.UserNotFoundException;
import com.org.ResolveIt.model.Feedback;
import com.org.ResolveIt.model.UserInfo;
import com.org.ResolveIt.repository.FeedbackRepository;
import com.org.ResolveIt.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public Feedback addFeedbackByUserId(Feedback feedback, Long userId) {

        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "userId does not exist",
                                "user is not found with this Id"
                        ));


        feedback.setUserInfo(userInfo);


        return feedbackRepository.save(feedback);
    }
    public List<Feedback> feedbackList(){
        return feedbackRepository.findAll();
    }

}
