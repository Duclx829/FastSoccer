package com.example.fastsoccer.repository;

import com.example.fastsoccer.entity.Booking;
import com.example.fastsoccer.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface BookingService extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.userId = :userId ORDER BY b.id DESC")
    List<Booking> findAllByUserId1(@Param("userId") UserEntity userId);

    List<Booking> findAllByPriceYardID_YardId_OwnPitch_IdAndStatusIsTrue(Long userId);

    /*
     * @author: HieuMM
     * @since: 06-Jul-22 11:39 AM
     * @description-VN:  hiển thị sân đã đặt trong ngày
     * @description-EN:
     * @param:
     * */
    @Query("select b.priceYardID.id from Booking b where b.dateBooking=?1 and b.priceYardID.yardId.id=?2 and b.status = true")
    List<Long> findAllPriceYardIsBooking(Date dateB, Long id);

    @Query("SELECT count(r.id) FROM Booking r")
    int countBooking();

    @Query("Select sum(b.priceYardID.price) from Booking b where b.status = true")
    int sumAmountBooking();

    @Query("Select sum(b.priceYardID.price) from Booking b where b.status = true and b.priceYardID.yardId.ownPitch.id=?1 ")
    double revenuePerYardList(Long idOwn);
    @Query("Select sum(b.priceYardID.price) from Booking b where b.status = true and b.priceYardID.yardId.ownPitch.id=?1 and b.dateBooking between ?1 and ?2")
    double revenuePerYardListByDate(Long idOwn, java.util.Date fromDate, java.util.Date toDate);


    @Query("SELECT count(r.id) FROM Booking r where r.priceYardID.yardId.ownPitch.id=?1")
    int countBookingPerYard(Long idOwn);
    @Query("SELECT count(r.id) FROM Booking r where r.priceYardID.yardId.ownPitch.id=?1 and r.dateBooking  between ?2 and ?3")
    int countBookingPerYardByDate(Long idOwn, java.util.Date fromDate, java.util.Date toDate);
}

