package com.org.ResolveIt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String description;
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "complaints_id")
    @JsonIgnore
    private Complaints complaint;
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_type", length = 50)
    private StatusType statusType;
}
