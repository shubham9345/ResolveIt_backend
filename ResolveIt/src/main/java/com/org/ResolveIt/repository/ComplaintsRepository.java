package com.org.ResolveIt.repository;

import com.org.ResolveIt.model.Complaints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintsRepository extends JpaRepository<Complaints,Long> {
    List<Complaints> findByUserId(Long userId);
}
