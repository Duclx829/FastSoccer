package com.example.fastsoccer.repository;

import com.example.fastsoccer.entity.OwnPitch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OwnPitchRepository extends JpaRepository<OwnPitch, Long> {
    @Query("SELECT u FROM OwnPitch u WHERE u.status=null")
    List<OwnPitch> findOwnPitchWatting();//sân chờ duyệt

    @Query("SELECT u FROM OwnPitch u WHERE u.status=true AND u.disable=true")
    List<OwnPitch> findOwnPitchSuccess();//sân đã được duyệt --b'1'

    @Query("SELECT u FROM OwnPitch u WHERE u.status=true AND u.disable=true AND u.district.id = ?1")
    List<OwnPitch> findOwnPitchSuccessByDistrictId(Long id);//sân đã được duyệt --b'1' theo districtId

    @Query("SELECT u FROM OwnPitch u WHERE u.status=true AND u.disable=true AND u.district.id not in ?1")
    List<OwnPitch> findOwnOtherPitchSuccessById(Long id);//sân đã được duyệt --b'1' theo district   Id

    OwnPitch findAllByPhone(String phone);

    @Query("SELECT count(r.id) FROM OwnPitch r")
    int countOwnPitch();

    //    @Query("SELECT u FROM OwnPitch u WHERE  u.status=true and u.disable=true and u.namePitch LIKE %:textSearch% ORDER BY u.namePitch ASC")

//    @Query("SELECT u, " +
//            "COALESCE((SELECT MIN((SELECT MIN(p.price) FROM PriceYard p WHERE p.yardId.id = y.id)) FROM Yard y WHERE y.ownPitch.id = u.id),0) AS minPrice, " +
//            "COALESCE((SELECT MAX((SELECT MIN(p.price) FROM PriceYard p WHERE p.yardId.id = y.id)) FROM Yard y WHERE y.ownPitch.id = u.id),0) AS maxPrice," +
//            "COALESCE((SELECT AVG(r.rate) FROM Review r WHERE r.ownPitch.id = u.id),0) AS avgRate\n" +
//            "FROM OwnPitch u \n" +
//            "WHERE u.status=true and u.disable=true and u.namePitch LIKE %:textSearch% ORDER BY u.namePitch ASC")
//    List<OwnPitch> search(@Param("textSearch") String title);
    @Query("SELECT u FROM OwnPitch u WHERE  u.status=true and u.disable=true and u.namePitch LIKE %:textSearch%")
    List<OwnPitch> search(@Param("textSearch") String title);

    @Query("SELECT u FROM OwnPitch u WHERE  u.status=true and u.disable=true and u.namePitch LIKE %:textSearch% AND u.district.id = :districtId")
    List<OwnPitch> searchByNameAndDistrictId(@Param("textSearch") String title, @Param("districtId") Long id    );

}
