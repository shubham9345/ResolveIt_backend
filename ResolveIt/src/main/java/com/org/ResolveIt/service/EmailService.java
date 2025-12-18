package com.org.ResolveIt.service;

import com.org.ResolveIt.model.StatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendComplaintStatusMail(String toEmail,
                                        String complaintTitle,
                                        StatusType status) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Complaint Status Updated");
        message.setText(
                "Hello,\n\n" +
                        "The status of your complaint has been updated.\n\n" +
                        "Complaint: " + complaintTitle + "\n" +
                        "Current Status: " + status + "\n\n" +
                        "Regards,\n complainX Support Team"
        );

        mailSender.send(message);
    }
}
