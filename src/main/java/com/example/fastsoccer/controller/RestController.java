package com.example.fastsoccer.controller;

import com.example.fastsoccer.entity.*;
import com.example.fastsoccer.repository.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.thymeleaf.util.StringUtils.substring;

@Controller
@RequestMapping
public class RestController {
    @Autowired
    YardRepository yardRepository;
    @Autowired
    PriceYardRepository priceYardRepository;
    /*
     * @author: HieuMM
     * @since: 06-Jul-22 1:48 PM
     * @description-VN:  Truyển vào ngày và id khung giờ của sân đã đặt, trả về thông tin booking của sân đó
     * @description-EN:
     * @param:
     * */
    @Autowired
    BookingService bookingService;
    @Autowired
    UserRepository userRepository;

    //        @GetMapping("/yard/getTime/{id}")
//    public ResponseEntity<?> getTime(@PathVariable("id") Long id) {
//            System.out.println(id);
//        return ResponseEntity.ok(priceYardRepository.findAllPriceYardByYardID(id));
//    }
    @GetMapping("/yard/{id}")
    public ResponseEntity<?> getPriceYardByYard(@PathVariable("id") Long id) {
        List<PriceYard> yard1 = priceYardRepository.findAllPriceYardByYardID(id);
        return ResponseEntity.ok(yard1);
    }

    @Autowired

    OwnPitchRepository ownPitchRepository;

//    @GetMapping("/getrevenue/{fromDate}/{toDate}")
//    public ResponseEntity<?> getRevenueByDate(@PathVariable(value = "fromDate") String fromDate, @PathVariable(value = "toDate") String toDate) {
//        List<OwnPitch> revenuePerYardList = ownPitchRepository.findAll();
//        for (int i = 0; i < revenuePerYardList.size(); i++) {
//            OwnPitch ownPitch = revenuePerYardList.get(i);
//            int countBookingPerYard = bookingService.countBookingPerYard(ownPitch.getId());
//            if (countBookingPerYard > 0) {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                try {
//                    ownPitch.setTotalRevenue(bookingService.revenuePerYardListByDate(ownPitch.getId(), sdf.parse(fromDate), sdf.parse(toDate)));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                ownPitch.setTotalRevenue(0);
//            }
//            revenuePerYardList.set(i, ownPitch);
//
//        }
//        System.out.println(revenuePerYardList.get(0).getTotalRevenue());
//        return ResponseEntity.ok(revenuePerYardList);
//    }

    /*
     * @author: HieuMM
     * @since: 07-Jul-22 3:24 PM
     * @description-VN:  Danh sách khung giờ đã được đặt  trong ngày theo id sân
     * @description-EN:
     * @param:
     * */

    @GetMapping("/getBooking/{fromDate}/{id}")
    public ResponseEntity<?> getBooking(@PathVariable(value = "fromDate") Date fromDate, @PathVariable(value = "id") Long id1) throws Exception {

        List<Long> booking = bookingService.findAllPriceYardIsBooking(fromDate, id1);
        List<PriceYard> priceYardsBooked = priceYardRepository.findAllYardReserved(booking, id1);
        List<PriceYard> priceYards = priceYardRepository.findAllPriceYardByYardID(id1);
        for (int i = 0; i < priceYards.size(); i++) {
            priceYards.get(i).setState(priceYardsBooked.indexOf(priceYards.get(i)) == -1);
        }
        return ResponseEntity.ok().body(priceYards);
    }

    @GetMapping("/getphonenumber/{phone}")
    public ResponseEntity<?> getPhone(@PathVariable(value = "phone") String phone) throws Exception {
        UserEntity userEntity = userRepository.findAllByUsername(phone);
        if (userEntity != null) {
            return ResponseEntity.ok().body(userEntity.getUsername());
        } else {
            return ResponseEntity.ok().body(null);
        }
    }


    @PostMapping("/api/removeMessage")
    public ResponseEntity<?> removeMessageFromSession(HttpSession session) {
        session.removeAttribute("message");
        session.removeAttribute("messagetype");
        return ResponseEntity.ok().body(null);
    }

    /*@GetMapping(value = "/sendSMS/{phone}")
    public ResponseEntity<String> sendSMS(@PathVariable(value = "phone") String phone) {
        Twilio.init("ACb451dd21c4c07f810dd8d7d3351678bf", "bb6c8342627a5f6602ea99c6e476bd86");
        String truePhone = substring(phone, 1);
        Message.creator(new PhoneNumber("+84" + truePhone),
                new PhoneNumber("+14845099386"), "Fast soccer chúc mừng bạn đăng kí thành công 📞").create();

        return new ResponseEntity<String>("Message sent successfully", HttpStatus.OK);
    }*/
    /*
     * @author: HieuMM
     * @since: 13-Jul-22 10:40 AM
     * @description-VN:  Lọc bài viết theo địa chỉ
     * @description-EN:
     * @param:
     * */
    @Autowired
    DistricRepository districRepository;

    @GetMapping("/postMatching/{id}")
    public ResponseEntity<?> getPostMatchingByDistrict(@PathVariable("id") Long id) {
        District district = districRepository.findById(id).get();
        return ResponseEntity.ok(district);
    }

}
