package com.example.Final.controller;

import com.example.Final.Dto.MailBody;
import com.example.Final.Repo.Forgotpasswordrepo;
import com.example.Final.Repo.UserRepository;
import com.example.Final.Service.EmailService;
import com.example.Final.Service.UserService;
import com.example.Final.config.ApiResponse;
import com.example.Final.config.JwtUtil;
import com.example.Final.model.ForgotPassword;
import com.example.Final.model.User;
import com.example.Final.util.ChangePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    private final Forgotpasswordrepo forgotpasswordrepo;
    @Autowired
    public UserController(Forgotpasswordrepo forgotpasswordrepo) {
        this.forgotpasswordrepo = forgotpasswordrepo;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {

        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Email already exists", null));
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Please enter a name", null));
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Please enter an email", null));
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Please enter a password", null));
        }

        userService.signUp(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userService.findByEmail(user.getEmail());

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Enter Valid Email", null));
        }

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Enter Valid Password", null));
        }

         String token = JwtUtil.generateToken(existingUser.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "Login successful", token));

    }

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<?> login(@PathVariable String email) {
        User user  = userRepository.findByEmail(email);
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "pls provide valid mail", null));
        }
        int otp = otpGen();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("this is the OTp for a email verficattion" + otp)
                .subject("OTP forgot password").build();
        ForgotPassword fb = ForgotPassword.builder()
                .otp(otp)
                .expireTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user).build();
        emailService.sendSimpleMessage(mailBody);
        forgotpasswordrepo.save(fb);
        return ResponseEntity.ok("Email send for a verfiaction");

    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<?> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user  = userRepository.findByEmail(email);
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "pls provide valid mail", null));
        }
            ForgotPassword fb = forgotpasswordrepo.findByOtpandUser(otp,user)
                      .orElseThrow(() ->new RuntimeException("invalid otp for email" + email));

        if(fb.getExpireTime().before(Date.from(Instant.now()))){
            forgotpasswordrepo.deleteById(fb.getFbid());
            return   ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse(false,"OTP has Expired",null));
        }
         return ResponseEntity.ok("Otp Verfied Sucess");
    }
    private Integer otpGen(){
        int min = 10000;
        int max = 999999;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword, @PathVariable String email){
           if(!Objects.equals(changePassword.password(), changePassword.rebeatpassword())){
               return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                       .body(new ApiResponse(false, "pls enter password again", null));
           }

           String encodedPassword  = passwordEncoder.encode(changePassword.password());
           userRepository.updatePassword(email,encodedPassword);
           return ResponseEntity.ok("Password has been changed");
    }


    //cart Controller


    /*@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }*/



}
