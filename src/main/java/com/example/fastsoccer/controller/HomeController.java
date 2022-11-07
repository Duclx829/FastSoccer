package com.example.fastsoccer.controller;

import com.example.fastsoccer.config.Config;
import com.example.fastsoccer.entity.*;
import com.example.fastsoccer.repository.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.hibernate.sql.InFragment.NULL;
import static org.thymeleaf.util.StringUtils.substring;

//trang home
@Controller
public class HomeController {
    //config twitlo
    @Value("${ACCOUNT_SID}")
    String ACCOUNT_SID;
    @Value("${AUTH_ID}")
    String AUTH_ID;
    @Value("${Phone_Twilio}")
    String Phone_Twilio;
    private final static int ITEMS_PER_PAGE = 24;
    @Autowired
    OwnPitchRepository ownPitchRepository;
    @Autowired
    DistricRepository districRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingService bookingService;
    @Autowired
    YardRepository yardRepository;
    @Autowired
    PriceYardRepository priceYardRepository;
    @Autowired
    PostRepository postRepository;

    @GetMapping("/loadPage")
    public String loadPage(Model model, HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        List<OwnPitch> ownPitchListOk = new ArrayList<>();
        //hiển thị sân gợi ý
        if (userEntity != null && userEntity.getDistrict() != null) {
//            nếu đã đăng nhập thì hiển thị sân gợi ý theo quận huyện của user
            ownPitchListOk = ownPitchRepository.findOwnPitchSuccessByDistrictId(userEntity.getDistrict().getId());
            ownPitchListOk = getExtendedDetailForOwnPitchs(ownPitchListOk);
            //sap xep theo rate
            ownPitchListOk = sortListOwnPitchByRateDesc(ownPitchListOk);
            if (ownPitchListOk.size() < 4) {
                List<OwnPitch> ownPithListOkExtend = ownPitchRepository.findOwnOtherPitchSuccessById(userEntity.getDistrict().getId());
                //lay them data
                ownPithListOkExtend = getExtendedDetailForOwnPitchs(ownPithListOkExtend);
                ownPithListOkExtend = sortListOwnPitchByRateDesc(ownPithListOkExtend);
                ownPitchListOk.addAll(ownPithListOkExtend.subList(0, ownPithListOkExtend.size() >= (4 - ownPitchListOk.size()) ? (4 - ownPitchListOk.size()) : ownPithListOkExtend.size()));
            } else {
                ownPitchListOk = ownPitchListOk.subList(0, 4);
            }
        } else {
            ownPitchListOk = ownPitchRepository.findOwnPitchSuccess();
            ownPitchListOk = getExtendedDetailForOwnPitchs(ownPitchListOk);
            ownPitchListOk = sortListOwnPitchByRateDesc(ownPitchListOk);
            ownPitchListOk = ownPitchListOk.subList(0, ownPitchListOk.size() < 4 ? ownPitchListOk.size() : 4);
        }

        model.addAttribute("ownPitchListOk", ownPitchListOk);
        session.setAttribute("menuActive", "home");
        return "index";
    }

    private List<OwnPitch> getExtendedDetailForOwnPitchs(List<OwnPitch> list) {
        for (int i = 0; i < list.size(); i++) {
            OwnPitch ownPitch = list.get(i);
            ownPitch.setMinPrice(priceYardRepository.getMinPriceYardByPitchId(ownPitch.getId()));
            ownPitch.setMaxPrice(priceYardRepository.getMaxPriceYardByPitchId(ownPitch.getId()));
            ownPitch.setAvgRate(reviewRepository.avgReview(ownPitch.getId()));
            list.set(i, ownPitch);
        }
        return list;
    }

    private List<OwnPitch> sortListOwnPitchByRateDesc(List<OwnPitch> list) {
        list.sort((o1, o2) -> {
            return o1.getAvgRate() - o2.getAvgRate() < 0 ? 1 : o1.getAvgRate() - o2.getAvgRate() > 0 ? -1 : 0;
        });
        return list;
    }

    @GetMapping("/loadPageAfterLogin")
    public String loadPageAfterLogin(Model model, HttpSession session) {
        /*List<OwnPitch> ownPitchListOk = ownPitchRepository.findOwnPitchSuccess(); //hiển thị sân đã xác nhận
        model.addAttribute("ownPitchListOk", ownPitchListOk);
        List<District> districtList = districRepository.findAll();
        model.addAttribute("districtList", districtList);
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        model.addAttribute("user", userEntity);
        int countUser = userRepository.countReview();
        model.addAttribute("countUser", countUser);*/
        return "redirect:/loadPage";
    }

    @GetMapping("/loadFormLogin")
    public String loadFormLogin(HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        if (userEntity != null) {
            switch (userEntity.getRole()) {
                case "ADMIN": {
                    return "redirect:/admin";
                }
                case "OWN": {
                    return "redirect:/load-manager-own";
                }

                case "USER": {
                    return "redirect:/loadPage";
                }
            }
        }
        return "loginform";
    }


    @GetMapping("/loadFormRegister")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    @PostMapping("/process_register")
    public String processRegister(UserEntity user, HttpSession session) {
        List<String> listUsername = userRepository.getListUsername();
        if (listUsername.contains(user.getUsername())) {
            session.setAttribute("message", "Số điện thoại đã được sử dụng!");
            return "redirect:/loadFormRegister";
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRole("USER");
            userRepository.save(user);
            //gưi tinh nhắn thông báo đến user
            try {
                Twilio.init(ACCOUNT_SID, AUTH_ID);
                String truePhone = substring(user.getUsername(), 1);
                Message.creator(new PhoneNumber("+84" + truePhone),
                        new PhoneNumber(Phone_Twilio), "Fast soccer chuc mung ban da đang ki tai khoan thanh cong.").create();
            } catch (Exception ex) {
            }

        }
        return "redirect:/loadFormLogin";
    }

    @GetMapping("/logout-success")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/loadFormLogin";
    }

    @GetMapping("/showDetail")
    public String showDetail(Model model, HttpSession session, @RequestParam("id") Long id,
                             @RequestParam(value = "page", required = false, defaultValue = "") String pageIndex) {
        int numberOfReviewOnPage = 5;
        int maxPage = 1;
        //  ModelAndView mav = new ModelAndView("single");
        Booking booking = (Booking) session.getAttribute("booking");
        if (booking != null) {
            session.removeAttribute("booking");
            model.addAttribute("booking", booking);
        }
        if (pageIndex.isEmpty()) {
            model.addAttribute("show", "description");

        } else {
            model.addAttribute("show", "review");
            model.addAttribute("page", pageIndex);
        }
        OwnPitch ownPitch = ownPitchRepository.findById(id).get();
        ownPitch.setMinPrice(priceYardRepository.getMinPriceYardByPitchId(id));
        ownPitch.setMaxPrice(priceYardRepository.getMaxPriceYardByPitchId(id));
        ownPitch.setAvgRate(Double.parseDouble(new DecimalFormat("0.0").format(reviewRepository.avgReview(id))));
        model.addAttribute("ownPitch", ownPitch);

        List<Yard> yardList = yardRepository.findAllByOwnPitch(id);
        model.addAttribute("yardList", yardList);
        List<PriceYard> priceYardList = priceYardRepository.findAllByYardId_OwnPitch_Id(id);
        model.addAttribute("priceYardList", priceYardList);
        List<Review> reviewList = reviewRepository.findAllByOwnPitch_Id(id);
        maxPage = reviewList.size() % numberOfReviewOnPage == 0 ? reviewList.size() / numberOfReviewOnPage : (reviewList.size() / numberOfReviewOnPage) + 1;
        if (maxPage > 1) {
            if (pageIndex.isEmpty()) {
                pageIndex = "1";
            }
            int firstIndex = (Integer.parseInt(pageIndex) - 1) * numberOfReviewOnPage;
            int lastIndex = Integer.parseInt(pageIndex) * numberOfReviewOnPage;
            lastIndex = lastIndex > reviewList.size() ? reviewList.size() : lastIndex;
            reviewList = reviewList.subList(firstIndex, lastIndex);
        }
        model.addAttribute("district", districRepository.findDistrictById(ownPitch.getDistrict().getId()));
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("totalReview", reviewRepository.countReviewByOwnPitchId(id));
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("maxPage", maxPage);
        session.setAttribute("menuActive", "pitch");
        return "pitchDetail1";
    }

    @GetMapping("/loadbyyard")
    public String loadbyyard(Model model, HttpSession session, @RequestParam("id") Long id) {
        List<PriceYard> priceYardList = priceYardRepository.findAllByYardId(id);
        model.addAttribute("priceYardList", priceYardList);
        session.setAttribute("menuActive", "pitch");
        return "pitchDetail1";
    }

    @PostMapping("/booking")
    public String bookingssss(@ModelAttribute("obj") Booking booking, @RequestParam("pitchId") Long pitchId, HttpSession session) throws UnsupportedEncodingException {
        PriceYard priceYard = priceYardRepository.findById(booking.getId()).get();
        //   booking.setId(null);
        booking.setPriceYardID(priceYard);
        booking.setStatus(false);
        session.setAttribute("ownPitchId", pitchId);
      /*  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();*/
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        booking.setUserId(userEntity);
        booking = bookingService.save(booking);
//        if (userEntity.getBookingList() != null) {
//            userEntity.getBookingList().add(booking);
//        } else {
//            userEntity.setBookingList(new ArrayList<Booking>());
//            userEntity.getBookingList().add(booking);
//        }
//        userEntity.getBookingList().add(booking);

        userRepository.save(userEntity);
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = booking.getUserId().getUsername();
        String orderType = "100000";
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = Config.vnp_TmnCode;

        int amount = (int) (booking.getPriceYardID().getPrice() * 100);
        Map vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        String bank_code = "NCB";
        if (bank_code != null && !bank_code.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bank_code);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_Returnurl + booking.getId());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        //Billing
        vnp_Params.put("vnp_Bill_Mobile", "09123891");
        vnp_Params.put("vnp_Bill_Email", "maiminhhieu1999@gmail.com");
        String fullName = "Quang dz".trim();
        if (fullName != null && !fullName.isEmpty()) {
            int idx = fullName.indexOf(' ');
            String firstName = fullName.substring(0, idx);
            String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
            vnp_Params.put("vnp_Bill_FirstName", firstName);
            vnp_Params.put("vnp_Bill_LastName", lastName);

        }
        vnp_Params.put("vnp_Bill_Address", "Test thui");
        vnp_Params.put("vnp_Bill_City", "Ha Noi");
        vnp_Params.put("vnp_Bill_Country", "Viet Nam");
//        if (req.getParameter("txt_bill_state") != null && !req.getParameter("txt_bill_state").isEmpty()) {
//            vnp_Params.put("vnp_Bill_State", req.getParameter("txt_bill_state"));
//        }
        // Invoice
        vnp_Params.put("vnp_Inv_Phone", "0963089510");
        vnp_Params.put("vnp_Inv_Email", "coolquanghuu@gmail.com");
        vnp_Params.put("vnp_Inv_Customer", "nguyen huu quang");
        vnp_Params.put("vnp_Inv_Address", "Ha Noi");
        vnp_Params.put("vnp_Inv_Company", "CY");
        vnp_Params.put("vnp_Inv_Taxcode", "32222");
//        vnp_Params.put("vnp_Inv_Type", req.getParameter("cbo_inv_type"));
        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
        com.google.gson.JsonObject job = new JsonObject();
        job.addProperty("code", "00");
        job.addProperty("message", "success");
        job.addProperty("data", paymentUrl);
        Gson gson = new Gson();
        return "redirect:" + paymentUrl;
    }

    @GetMapping("/myBooking")
    public String myBooking(HttpSession session, Model model) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        if (userEntity == null) {
            return "redirect:/loadFormLogin";
        } else {
            List<Booking> bookingList = bookingService.findAllByUserId1(userEntity);
            model.addAttribute("bookingList", bookingList);
            return "myBooking";
        }
    }

    @GetMapping("/user/pay")
    public String payvn(@RequestParam("id") Long id, Model model, HttpSession session, @RequestParam("vnp_ResponseCode") String vnp_ResponseCode) {
        if (!vnp_ResponseCode.equals("00")) {
            Long ownPitchId = (Long) session.getAttribute("ownPitchId");
            session.removeAttribute("ownPitchId");
            return "redirect:/showDetail?id=" + ownPitchId;
        } else {
            Booking booking = bookingService.findById(id).get();
            booking.setStatus(true);
            bookingService.save(booking);
            //gưi tinh nhắn thông báo đến
            UserEntity userEntity = (UserEntity) session.getAttribute("user");
            Twilio.init(ACCOUNT_SID, AUTH_ID);
            String userPhoneNumber = substring(userEntity.getUsername(), 1);
            String ownPitchPhoneNumber = substring(booking.getPriceYardID().getYardId().getOwnPitch().getPhone(), 1);
            try {
                Message.creator(new PhoneNumber("+84" + userPhoneNumber),
                        new PhoneNumber(Phone_Twilio), "Fast soccer thông tin đặt sân: " + booking.getPriceYardID().getYardId().getName() + " thời gian: " + booking.getPriceYardID().getStartTime() + "-" + booking.getPriceYardID().getEndTime() + "(" + booking.getDateBooking() + ").").create();
                Message.creator(new PhoneNumber("+84" + ownPitchPhoneNumber),
                        new PhoneNumber(Phone_Twilio), "Fast soccer: " + booking.getPriceYardID().getYardId().getName() + " thời gian: " + booking.getPriceYardID().getStartTime() + "-" + booking.getPriceYardID().getEndTime() + "(" + booking.getDateBooking() + ") đã được đặt bởi sdt: " + userPhoneNumber + ".").create();
                //hien thi file html
            } catch (Exception ex) {
            }

            model.addAttribute("booking", booking);
            return "bookingsuccess";
        }
    }

    @GetMapping("/loadPost")
    public String loadPost(Model model) {
        List<Post> postList = postRepository.findAll();

        model.addAttribute("postList", postList);
        return "post";
    }

    @PostMapping("/postMatching")
    public String postMatching(Post post, HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        post.setPublicationTime(date);
        post.setUserEntity(userEntity);
        postRepository.save(post);
        session.setAttribute("message", "Đăng bài viết thành công.");
        session.setAttribute("messagetype", "success");
        return "redirect:/loadMatching";
    }

    @GetMapping("/loadMatching")
    public String loadMatching(Model model, HttpSession session) {
        session.setAttribute("menuActive", "matching");
        List<District> districtList = districRepository.findAll();
        model.addAttribute("districtList", districtList);
        List<Post> postList = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("postList", postList);
        return "matching";
    }

    @GetMapping("/loadMatchByAdd")
    public String loadMatchByAdd(Model model, HttpSession session, @RequestParam("id") Long addressId) {
        List<Post> postList = postRepository.findAllByDistrictEntity_Id(addressId);
        model.addAttribute("postList", postList);
        session.setAttribute("menuActive", "matching");
        return "matching";
    }

    @GetMapping("/deletePost")
    public String deletePost(@RequestParam("id") Long id, HttpSession session) {
        session.setAttribute("tab", "post");
        session.setAttribute("message", "Xóa bài viết thành công.");
        session.setAttribute("messagetype", "success");
        postRepository.deleteById(id);
        return "redirect:/loadUserProfile";
    }

    @GetMapping("/loadUserProfile")
    public String loadUserProfile(HttpSession session, Model model) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        if (userEntity == null) {
            return "redirect:/loadFormLogin";
        } else {
            String tabActive = (String) session.getAttribute("tab");
            if (tabActive == null) {
                tabActive = "profile";
            } else {
                session.removeAttribute("tab");
            }
            model.addAttribute("tabActive", tabActive);
            List<District> districtList = districRepository.findAll();
            model.addAttribute("districtList", districtList);
            model.addAttribute("userProfile", userEntity);

            //hiển thị sân đã đặt của người dùng
            List<Booking> bookingList = bookingService.findAllByUserId1(userEntity);
            model.addAttribute("bookingList", bookingList);
            session.setAttribute("menuActive", "profile");
            //hiển thị bài viết đã đăng của người dùng
            List<Post> postList = postRepository.findAllByUserEntity(userEntity);
            model.addAttribute("postList", postList);
            return "userProfile";
        }
    }

    @GetMapping("/sendOTPChangePassword")
    public String sendOTPChangePassword(HttpSession session, Model model) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        if (userEntity == null) {
            return "redirect:/loadFormLogin";
        } else {
            int otp = (int) (Math.random() * 10000);
            session.setAttribute("otp", otp);
//            userRepository.save(userEntity);
            String message = "OTP của bạn là: " + otp;
            //gưi tinh nhắn thông báo đến user
            try {
                Twilio.init(ACCOUNT_SID, AUTH_ID);
                String truePhone = substring(userEntity.getUsername(), 1);
                Message.creator(new PhoneNumber("+84" + truePhone),
                        new PhoneNumber(Phone_Twilio), message).create();
            } catch (Exception ex) {
            }
            model.addAttribute("otp", otp);
            return "changePassword";
        }
    }
    /*
     * @author: HieuMM
     * @since: 14-Jul-22 2:55 PM
     * @description-VN:  Thay đổi mật khẩu nếu đúng OTP
     * @description-EN:
     * @param:
     * */

    @PostMapping("/changePassword")
    public String changePassword(Model model, @RequestParam("password") String password, @RequestParam("otp") Integer otp, HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        if (!session.getAttribute("otp").equals(otp)) {
            model.addAttribute("alert", "alert");
            return "changePassword";
        } else if (session.getAttribute("otp").equals(otp)) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(password);
            userEntity.setPassword(encodedPassword);
            userEntity.setToken(null);
            userRepository.save(userEntity);
            session.removeAttribute("otp");
            return "redirect:/logout-success";
        } else {
            return "redirect:/logout-success";
        }
    }

    @GetMapping("/loadFormforgotPassword")
    public String loadFormforgotPassword() {
        return "forgotPassword";
    }

    /*
     * @author: HieuMM
     * @since: 14-Jul-22 2:54 PM
     * @description-VN:  Gửi mật khẩu mới cho user
     * @description-EN:
     * @param:
     * */
    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("username") String username, HttpSession session) {
        UserEntity userEntity = userRepository.findAllByUsername(username);
        if (userEntity == null) {
            session.setAttribute("userNotExit", "Tài khoản không tồn tại !");
            return "redirect:/loadFormforgotPassword";
        } else {
            String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi"
                    + "jklmnopqrstuvwxyz";
            Random rnd = new Random();
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++)
                sb.append(chars.charAt(rnd.nextInt(chars.length())));
            //System.out.printf(sb.toString());
            String password = sb.toString();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(password);
            userEntity.setPassword(encodedPassword);
            userRepository.save(userEntity);
            String message = "Mật khẩu mới của bạn là:  " + password;
            //gưi tinh nhắn thông báo đến user
            Twilio.init(ACCOUNT_SID, AUTH_ID);
            try {
                String truePhone = substring(userEntity.getUsername(), 1);
                Message.creator(new PhoneNumber("+84" + truePhone),
                        new PhoneNumber(Phone_Twilio), message).create();
            }catch (Exception ex){}

            return "redirect:/loadFormLogin";
        }
    }

    @Autowired
    private ReviewRepository reviewRepository;

    /*
     * @author: HieuMM
     * @since: 04-Aug-22 6:22 PM
     * @description-VN:  Đánh giá sân
     * @description-EN:
     * @param:
     * */
    @GetMapping("/showFormReview")
    public String loadFormReview(Model model, HttpSession session, @RequestParam("id") Long id) {
        Booking booking = bookingService.findById(id).get();
        model.addAttribute("booking", booking);
        session.setAttribute("booking", booking);
        Long idOwner = booking.getPriceYardID().getYardId().getOwnPitch().getId();
        return "redirect:/showDetail?page=1&id=" + idOwner;
    }

    @PostMapping("/postReview")
    public String postReview(@RequestParam("idOwnPitch") Long idOwnPitch, @RequestParam("idBooking") Long idBooking, Review review, HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        OwnPitch ownPitch = ownPitchRepository.findById(idOwnPitch).get();
        review.setUser(userEntity);
        review.setOwnPitch(ownPitch);
        Booking booking = bookingService.findById(idBooking).get();
        booking.setIsReview(true);
        bookingService.save(booking);
        session.setAttribute("message", "Gửi đánh giá thành công.");
        session.setAttribute("messagetype", "success");
        reviewRepository.save(review);
        return "redirect:/showDetail?page=1&id=" + ownPitch.getId();
    }

    @GetMapping("/loadFind")
    public String loadFind(Model model, HttpSession session,
                           @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                           @RequestParam(name = "search", required = false, defaultValue = "") String textSearch,
                           @RequestParam(name = "province", required = false, defaultValue = "") String province,
                           @RequestParam(name = "district", required = false, defaultValue = "") String districtId,
                           @RequestParam(name = "order", required = false, defaultValue = "") String order,
                           @RequestParam(name = "sortby", required = false, defaultValue = "") String sortBy) {
        int maxPage = 1;
        model.addAttribute("history", textSearch.trim());
        model.addAttribute("province", province);
        model.addAttribute("district", districtId);
        model.addAttribute("order", order);
        model.addAttribute("sortby", sortBy);
        List<OwnPitch> searchListOwnPitch = new ArrayList<>();
        if (districtId.isEmpty()) {
            searchListOwnPitch = ownPitchRepository.search(textSearch);
        } else {
            model.addAttribute("districtName", districRepository.findDistrictById(Long.valueOf(districtId)).getName());
            searchListOwnPitch = ownPitchRepository.searchByNameAndDistrictId(textSearch, Long.valueOf(districtId));
        }

        //get more detail for pitch
        for (int i = 0; i < searchListOwnPitch.size(); i++) {
            OwnPitch ownPitch = searchListOwnPitch.get(i);
            ownPitch.setMinPrice(priceYardRepository.getMinPriceYardByPitchId(ownPitch.getId()));
            ownPitch.setMaxPrice(priceYardRepository.getMaxPriceYardByPitchId(ownPitch.getId()));
            ownPitch.setAvgRate(reviewRepository.avgReview(ownPitch.getId()));
            searchListOwnPitch.set(i, ownPitch);
        }

        //Fake data
//        for (int i = 0; i < 2; i++) {
//            searchListOwnPitch.addAll(searchListOwnPitch);
//        }

        //sort list pitch by rate in descending order then sort by pitch name in ascending order
        searchListOwnPitch.sort((o1, o2) -> {
            return sortBy.equalsIgnoreCase("price")
                    ?
                    (o1.getMaxPrice() - o2.getMaxPrice() < 0 ? (order.equalsIgnoreCase("asc") ? -1 : 1)
                            : o1.getMaxPrice() - o2.getMaxPrice() > 0 ? (order.equalsIgnoreCase("asc") ? 1 : -1)
                            : o1.getMinPrice() - o2.getMinPrice() < 0 ? (order.equalsIgnoreCase("asc") ? -1 : 1)
                            : o1.getMinPrice() - o2.getMinPrice() > 0 ? (order.equalsIgnoreCase("asc") ? 1 : -1)
                            : o1.getAvgRate() - o2.getAvgRate() < 0 ? 1
                            : o1.getAvgRate() - o2.getAvgRate() > 0 ? -1
                            : o1.getNamePitch().compareTo(o2.getNamePitch()) > 0 ? 1
                            : o1.getNamePitch().compareTo(o2.getNamePitch()) < 0 ? -1 : 0
                    )
                    :
                    (o1.getAvgRate() - o2.getAvgRate() < 0 ? 1
                            : o1.getAvgRate() - o2.getAvgRate() > 0 ? -1
                            : o1.getNamePitch().compareTo(o2.getNamePitch()) > 0 ? 1
                            : o1.getNamePitch().compareTo(o2.getNamePitch()) < 0 ? -1 : 0
                    );
        });

        if (searchListOwnPitch.isEmpty()) {
            session.setAttribute("isEmpty", "Không có kết quả với nội dung tìm kiếm \"" + textSearch + "\"");
        } else {
            session.removeAttribute("isEmpty");
            maxPage = searchListOwnPitch.size() % ITEMS_PER_PAGE == 0 ? searchListOwnPitch.size() / ITEMS_PER_PAGE : (searchListOwnPitch.size() / ITEMS_PER_PAGE) + 1;
            if (maxPage > 1) {
                int startIndex = --page * ITEMS_PER_PAGE;
                int endIndex = ++page * ITEMS_PER_PAGE;
                searchListOwnPitch = searchListOwnPitch.subList(startIndex, searchListOwnPitch.size() < endIndex ? searchListOwnPitch.size() : endIndex);
            }
        }
        model.addAttribute("listDistrict", districRepository.findAll());
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("searchListOwnPitch", searchListOwnPitch);
        session.setAttribute("menuActive", "pitch");
        return "findPitch";
    }


    /*
     * @author: HieuMM
     * @since: 10-Aug-22 9:25 PM
     * @description-VN:
     * @description-EN:
     * @param:
     * */
    @Value("${config.upload_folder}")
    String UPLOAD_FOLDER;

    @PostMapping("/updateProfileUser")
    public String updateProfile(HttpSession session, UserEntity userEntity, @RequestParam("avatar") MultipartFile image) {
        UserEntity userEntityOld = (UserEntity) session.getAttribute("user");
        if (image.isEmpty()) {
            userEntityOld.setImage(userEntityOld.getImage());
        } else {
            String relativeFilePath1 = null;
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
                relativeFilePath1 = subFolder + Instant.now().getEpochSecond() + image.getOriginalFilename();
                Files.write(Paths.get(UPLOAD_FOLDER + relativeFilePath1), image.getBytes());
                userEntityOld.setImage(relativeFilePath1);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        userEntityOld.setDistrict(userEntity.getDistrict());
        userEntityOld.setFullName(userEntity.getFullName());
        userRepository.save(userEntityOld);
        session.setAttribute("message", "Cập nhật thông tin thành công.");
        session.setAttribute("messagetype", "success");
        return "redirect:/loadUserProfile";
    }

}

