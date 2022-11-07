package com.example.fastsoccer.repository;

import com.example.fastsoccer.entity.Booking;
import com.example.fastsoccer.entity.PriceYard;
import com.example.fastsoccer.entity.Yard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceYardRepository extends JpaRepository<PriceYard,Long> {
    List<PriceYard> findAllByYardId_OwnPitch_Id(Long idOwn);
    List<PriceYard> findAllByYardId(Long idYard);
    @Query("select pr from PriceYard pr where  pr.id not in ?1 and pr.yardId.id = ?2 and pr.activated=true  order by pr.startTime ASC")
    List<PriceYard> findAllYardNotReserved(List<Long> list, Long idYard);
    @Query("select pr from PriceYard pr where  pr.id in ?1 and pr.yardId.id = ?2 and pr.activated=true  order by pr.startTime ASC")
    List<PriceYard> findAllYardReserved(List<Long> list, Long idYard);

    @Query("select pr from Yard yr \n " +
            "join PriceYard pr on pr.yardId.id = yr.id \n " +
            "where yr.id = ?1 and pr.activated=true order by pr.startTime ASC")
    List<PriceYard> findAllPriceYardByYardID(Long idYard);

    @Query("SELECT COALESCE(MIN((SELECT MIN(p.price) FROM PriceYard p where p.yardId.id = y.id)),0) AS minPrice from Yard y where y.ownPitch.id = ?1")
    int getMinPriceYardByPitchId(Long id);

    @Query("SELECT COALESCE(MAX((SELECT MAX(p.price) FROM PriceYard p where p.yardId.id = y.id)),0) AS minPrice from Yard y where y.ownPitch.id = ?1")
    int getMaxPriceYardByPitchId(Long id);


    @Query("SELECT y FROM Yard y where y.ownPitch.id=?1 and y.isActivated=true order by y.id desc ")
    List<Yard> findAllByOwnPitch(Long id);

}
