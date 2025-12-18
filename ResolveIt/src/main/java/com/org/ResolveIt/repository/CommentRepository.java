package com.org.ResolveIt.repository;

import com.org.ResolveIt.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
