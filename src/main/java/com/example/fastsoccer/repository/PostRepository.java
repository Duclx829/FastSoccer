package com.example.fastsoccer.repository;

import com.example.fastsoccer.entity.Post;
import com.example.fastsoccer.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.userEntity = ?1 ORDER BY p.publicationTime DESC")
    List<Post> findAllByUserEntity(UserEntity userEntity);
    List<Post> findAllByDistrictEntity_Id(Long id);

}

