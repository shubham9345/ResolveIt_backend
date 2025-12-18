package com.org.ResolveIt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class CommentEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentEmpId;
    @ManyToOne
    @JoinColumn(name = "complaint_id")
    private Complaints complaintEmployee;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;
    @CreationTimestamp
    private LocalDate commentDate;
}
