package com.forumhub.repository;

import com.forumhub.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
