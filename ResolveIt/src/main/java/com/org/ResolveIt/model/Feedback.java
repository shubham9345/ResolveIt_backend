package com.org.ResolveIt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;
    private String feedback_desc;
    private Double ratings;
    @ManyToOne
    @JoinColumn(name = "userInfo_Id")
    private UserInfo userInfo;
    @CreationTimestamp
    private LocalDate FeedbackDate;
}
