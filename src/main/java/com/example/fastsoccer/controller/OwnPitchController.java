package com.example.fastsoccer.controller;

import com.example.fastsoccer.entity.*;
import com.example.fastsoccer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//các chức năng cho chủ sân bóng
@Controller
@MultipartConfig
/*@RequestMapping("/own")*/
public class OwnPitchController {
    @Autowired
    OwnPitchRepository ownPitchRepository;

    @Autowired
    DistricRepository districRepository;
    @Autowired
    YardRepository yardRepository;
    //đăng kí sân
    @Value("${config.upload_folder}")
    String UPLOAD_FOLDER;

    @Autowired
    PriceYardRepository priceYardRepository;

    @GetMapping("/showformRegisterPitch")
    public String loadDistrict(Model model) {
        List<District> districtList = districRepository.findAll();
        model.addAttribute("districtList", districtList);
        return "registerPitch";
    }
  /*  @PostMapping("/updateStatus")
    public String updateStatus(@ModelAttribute("obj") OwnPitch ownPitch) {
        ownPitchRepository.save(ownPitch);
        return "indexold.html";
    }*/

    @PostMapping("/registerPitch")
    public String addPro(@ModelAttribute("obj") OwnPitch ownPitch,
                         @RequestParam("pic1") MultipartFile file1,
                         @RequestParam("pic2") MultipartFile file2,
                         @RequestParam("pic3") MultipartFile file3) {
        String relativeFilePath1 = null;
        String relativeFilePath2 = null;
        String relativeFilePath3 = null;
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int day = localDate.getDayOfMonth();
        String subFolder = day + "_" + year + "/";
        String fullUploadDir = UPLOAD_FOLDER + subFolder;
        File checkDir = new File(fullUploadDir);
        if (!checkDir.exists() || checkDir.isFile()) {
            checkDir.mkdir();

        }
        try {
            relativeFilePath1 = subFolder + Instant.now().getEpochSecond() + file1.getOriginalFilename();
            Files.write(Paths.get(UPLOAD_FOLDER + relativeFilePath1), file1.getBytes());
            relativeFilePath2 = subFolder + Instant.now().getEpochSecond() + file2.getOriginalFilename();
            Files.write(Paths.get(UPLOAD_FOLDER + relativeFilePath2), file2.getBytes());
            relativeFilePath3 = subFolder + Instant.now().getEpochSecond() + file3.getOriginalFilename();
            Files.write(Paths.get(UPLOAD_FOLDER + relativeFilePath3), file3.getBytes());
            ownPitch.setImg1(relativeFilePath1);
            ownPitch.setImg2(relativeFilePath2);
            ownPitch.setImg3(relativeFilePath3);
        } catch (IOException e) {
            System.out.println("ko upload duoc");
            e.printStackTrace();
        }
        ownPitch.setStatus(null);
        ownPitchRepository.save(ownPitch);
        return "thankyou.html";
    }

    //trang thông tin chủ sân sau khi login
/*    @GetMapping("/homeOwn")
    public String homeOwn(Model model, @RequestParam("phone") Long phone) {
        OwnPitch ownPitch = ownPitchRepository.findAllByPhone(String.valueOf(phone));
        Long id = ownPitch.getId().longValue();
        List<Yard> yardList = yardRepository.findAllByOwnPitchId(id);
        model.addAttribute("yardList", yardList);
        return "homeOwn";
    }*/
    @Autowired
    BookingService bookingService;

    @RequestMapping("/load-manager-own")
    public String load(Model model, HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        model.addAttribute("user", userEntity);

        List<Booking> bookingList = bookingService.findAllByPriceYardID_YardId_OwnPitch_IdAndStatusIsTrue(userEntity.getIdOwn());
        // List<Booking> bookingList = bookingService.findAll();
        model.addAttribute("bookingList", bookingList);
        return "ownmanager";
    }

    @RequestMapping(value = "/loadyardmanagerown")
    public String loadYardManagerOwn(Model model, HttpSession session) {
        try {
            UserEntity userEntity = (UserEntity) session.getAttribute("user");
            session.setAttribute("yardSelected", null);
            //hiển thị tất cả sân nhỏ
            List<Yard> yardList = yardRepository.findAllByOwnPitch(userEntity.getIdOwn());
//        yardList.sort(Comparator.compari);
            model.addAttribute("user", userEntity);
            model.addAttribute("yardList", yardList);
            //hiển thị tất cả giá tiền ở sân nhỏ theo giờ
            List<PriceYard> priceYardList = priceYardRepository.findAllByYardId_OwnPitch_Id(userEntity.getIdOwn());
            model.addAttribute("priceYardList", priceYardList);
        }catch (Exception ew){
            System.out.println("LOI: "+ ew.getMessage());
        }

        return "ownyard";
    }

//    @GetMapping("/loadformaddyard")
//    public String loadformaddyard(Model model, HttpSession session) {
//        UserEntity userEntity = (UserEntity) session.getAttribute("user");
//        model.addAttribute("user", userEntity);
//        return "add-yard";
//    }

    //thêm sân nhỏ
    @PostMapping("/addyard")
    public String addYard(Yard yard) {
        yard.setActivated(true);
        yardRepository.save(yard);
        return "redirect:/loadyardmanagerown";
    }

    //xóa sân nhỏ
    @GetMapping("/deleteYard")
    public String deleteYard(@RequestParam("id") Long id) {
        Yard yard = yardRepository.getById(id);
        yard.setActivated(false);
        yardRepository.save(yard);
        return "redirect:/loadyardmanagerown";

    }


    @PostMapping("/addprice")
    public String addPrice(Model model, HttpSession session, PriceYard priceYard) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        //hiển thị tất cả sân nhỏ
        List<Yard> yardList = yardRepository.findAllByOwnPitch(userEntity.getIdOwn());
        model.addAttribute("user", userEntity);
        model.addAttribute("yardList", yardList);
        //hiển thị tất cả giá tiền ở sân nhỏ theo giờ
        List<PriceYard> priceYardList = priceYardRepository.findAllByYardId_OwnPitch_Id(userEntity.getIdOwn());
        model.addAttribute("priceYardList", priceYardList);
        session.setAttribute("yardSelected", priceYard.getYardId().getId());
        priceYard.setActivated(true);
        priceYardRepository.save(priceYard);
        return "ownyard";
    }

    //xóa giá sân
    @GetMapping("/deletePrice")
    public String deletePrice(@RequestParam("id") Long id) {
        PriceYard priceYard = priceYardRepository.getById(id);
        priceYard.setActivated(false);
        priceYardRepository.save(priceYard);
        return "redirect:/loadyardmanagerown";

    }
}
