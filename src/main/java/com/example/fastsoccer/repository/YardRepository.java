package com.example.fastsoccer.repository;

import com.example.fastsoccer.entity.Yard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YardRepository extends JpaRepository<Yard, Long> {
   /* @Query("SELECT y FROM Yard y WHERE y.ownPitch.id=:id"  )
    List<Yard> findByOwnPitchId(@Param("id") Long id);*/

    List<Yard> findAllByOwnPitch_Id(Long id);


  //  List<Yard> findAllByOwnPitch_IdAndActivatedIsTrueOrderByIdDesc(Long id);


    @Query("SELECT y FROM Yard y where y.ownPitch.id=?1 and y.isActivated=true order by y.id desc ")
    List<Yard> findAllByOwnPitch(Long id);
}

