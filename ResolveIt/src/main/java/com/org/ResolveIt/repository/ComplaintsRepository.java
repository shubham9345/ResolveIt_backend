package com.org.ResolveIt.repository;

import com.org.ResolveIt.model.Complaints;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintsRepository extends JpaRepository<Complaints,Long> {
}
