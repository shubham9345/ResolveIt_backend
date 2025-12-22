package com.org.ResolveIt.repository;

import com.org.ResolveIt.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
}
