package com.example.fastsoccer.controller;

import com.example.fastsoccer.entity.OwnPitch;
import com.example.fastsoccer.entity.UserEntity;
import com.example.fastsoccer.repository.BookingService;
import com.example.fastsoccer.repository.OwnPitchRepository;
import com.example.fastsoccer.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;

import static java.sql.Types.NULL;
import static org.thymeleaf.util.StringUtils.substring;

//chức năng cho quản trị viên
@Controller
/*@RequestMapping("/admin")*/
public class AdminController {
    //config twitlo
    @Value("${ACCOUNT_SID}")
    String ACCOUNT_SID;
    @Value("${AUTH_ID}")
    String AUTH_ID;
    @Value("${Phone_Twilio}")
    String Phone_Twilio;
    @Autowired
    OwnPitchRepository ownPitchRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingService bookingService;

    @GetMapping("/admin")
    public String test(Model model) {
        //tổng số người dùng
        int countUser = userRepository.countUser();
        model.addAttribute("counUser", countUser);
        //tổng số sân
        int countOwnPitch = ownPitchRepository.countOwnPitch();
        model.addAttribute("counOwnPitch", countOwnPitch);
        //tổng số lịch đặt sân
        int countBooking = bookingService.countBooking();
        model.addAttribute("countBooking", countBooking);
        //tổng doanh thu
        if (countBooking > 0) {
            double sumAmountBooking = bookingService.sumAmountBooking() * 0.05;
            model.addAttribute("sumAmountBooking", sumAmountBooking);
        } else {
            int sumAmountBooking = 0;
            model.addAttribute("sumAmountBooking", sumAmountBooking);
        }

        //doanh thu từng sân
        HashMap<OwnPitch, Double> revenuePerYardList = new HashMap<OwnPitch, Double>();
        for (OwnPitch ownPitch : ownPitchRepository.findAll()) {
            int countBookingPerYard = bookingService.countBookingPerYard(ownPitch.getId());
            if (countBookingPerYard > 0) {
                double revenuePerYard = bookingService.revenuePerYardList(ownPitch.getId());
                revenuePerYardList.put(ownPitch, revenuePerYard);
            } else {
                revenuePerYardList.put(ownPitch, (double) 0);
            }
        }
        model.addAttribute("revenuePerYardList", revenuePerYardList);

        return "admin/dashboard";
    }

    @GetMapping("/frofileAdmin")
    public String profileAdmin(Model model) {
        List<UserEntity> userEntities = userRepository.findAll();
        model.addAttribute("userList", userEntities);
        return "admin/user";
    }


//hiển thị danh sách sân chờ duyệt

    //detail sân chờ duyệt

    @GetMapping("/loadPitchNotAllow")
    public String loadPitchNotAllow(Model model) {
        List<OwnPitch> ownPitchListOk = ownPitchRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("ownPitchListOk", ownPitchListOk);
        return "admin/tables";
    }

    //xem thông tin đầy đủ của sân và xét duyệt
    @GetMapping("/update")
    public ModelAndView update(Model model, @RequestParam("id") Long id) {
        ModelAndView mav = new ModelAndView("admin/detail");
        OwnPitch ownPitch = ownPitchRepository.findById(id).get();
        mav.addObject("ownPitch", ownPitch);
        return mav;
    }

    //xét duyệt sân
    @PostMapping("/updateStatus")
    public String updateStatus(@ModelAttribute("obj") OwnPitch ownPitch) {
        ownPitch.setStatus(false);
        ownPitchRepository.save(ownPitch);
        try {
            Twilio.init(ACCOUNT_SID, AUTH_ID);
            String truePhone = substring(ownPitch.getPhone(), 1);
            Message.creator(new PhoneNumber("+84" + truePhone),
                    new PhoneNumber(Phone_Twilio), "Fast soccer chuc mung san da duoc duyet " + ownPitch.getNamePitch() + ". He thong se gui thong tin tai khoan vao tin nhan tiep theo.").create();
        } catch (Exception ex) {
        }

        return "redirect:/loadPitchNotAllow";
    }

    //chuyển sang trang tạo tài khoản cho chủ sân bóng
    @GetMapping("/createacount")
    public ModelAndView createAcount(Model model, @RequestParam("id") Long id) {
        ModelAndView mav = new ModelAndView("admin/createAccountOwn");
        OwnPitch ownPitch = ownPitchRepository.findById(id).get();
        mav.addObject("ownPitch", ownPitch);
        model.addAttribute("user", new UserEntity());
        return mav;
    }
    //tạo tài khoản cho chủ sân bóng

    @PostMapping("/createAccountOwn")
    public String processRegister(UserEntity user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        OwnPitch ownPitch = ownPitchRepository.findById(user.getIdOwn()).get();
        ownPitch.setStatus(true);
        ownPitch.setDisable(true);
        ownPitchRepository.save(ownPitch);
        user.setPassword(encodedPassword);
        user.setRole("OWN");
        userRepository.save(user);
        try {
            Twilio.init(ACCOUNT_SID, AUTH_ID);
            String truePhone = substring(ownPitch.getPhone(), 1);
            Message.creator(new PhoneNumber("+84" + truePhone),
                    new PhoneNumber(Phone_Twilio), "Fast soccer thong bao tai khoan: " + ownPitch.getPhone() + " va mat khau: " + ownPitch.getPhone()).create();
        } catch (Exception ex) {
        }
        return "redirect:/loadPitchNotAllow";
    }

    /*
     * @author: HieuMM
     * @since: 22-Jul-22 8:56 AM
     * @description-VN:  Ẩn sân bóng
     * @description-EN:
     * @param:
     * */
    @GetMapping("/disablePitch")
    public String disablePitch(@RequestParam("id") Long id) {
        OwnPitch ownPitch = ownPitchRepository.findById(id).get();
        ownPitch.setDisable(false);
        ownPitchRepository.save(ownPitch);
        return "redirect:/loadPitchNotAllow";
    }

    /*
     * @author: HieuMM
     * @since: 22-Jul-22 8:56 AM
     * @description-VN:  Hiện sân bóng
     * @description-EN:
     * @param:
     * */
    @GetMapping("/undisablePitch")
    public String undisablePitch(@RequestParam("id") Long id) {
        OwnPitch ownPitch = ownPitchRepository.findById(id).get();
        ownPitch.setDisable(true);
        ownPitchRepository.save(ownPitch);
        return "redirect:/loadPitchNotAllow";
    }

    /*
     * @author: HieuMM
     * @since: 28-Jul-22 8:44 PM
     * @description-VN:  Xóa thông tin sân
     * @description-EN:
     * @param:
     * */
    @GetMapping("/deletePitch")
    public String deletePitch(@RequestParam("id") Long id) {
        ownPitchRepository.deleteById(id);
        return "redirect:/loadPitchNotAllow";
    }


}
