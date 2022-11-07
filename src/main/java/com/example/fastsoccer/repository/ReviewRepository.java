package com.example.fastsoccer.repository;

import com.example.fastsoccer.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.ownPitch.id = ?1 ORDER BY r.id DESC")
    List<Review> findAllByOwnPitch_Id(Long id);
    @Query("SELECT COALESCE(AVG(r.rate), 0) AS avgReview FROM Review r WHERE r.ownPitch.id = ?1")
     double avgReview(Long id);

    int countReviewByOwnPitchId(Long id);
}
