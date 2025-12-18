package com.org.ResolveIt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Complaints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintsId;
    private boolean isAnonymous;
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;
    @Enumerated(EnumType.STRING)
    private StatusType statusType;
    private String description;
    @Enumerated(EnumType.STRING)
    private UrgencyType urgencyType;
    @CreationTimestamp
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long userId;
    private String attachedUrl;
    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee assignedEmployee;

}
