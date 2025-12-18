package com.org.ResolveIt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    private Long id;
    private LocalDate joinDate;
    private String Dob;
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;
    @OneToMany(mappedBy = "assignedEmployee", fetch = FetchType.LAZY)
    private List<Complaints> allComplaints;
    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id")
    private UserInfo user;
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<CommentEmployee> commentEmployeeList = new ArrayList<>();
}
