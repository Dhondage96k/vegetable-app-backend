package com.rohit.vegetable_app.service;

import com.rohit.vegetable_app.Model.Otp;
import com.rohit.vegetable_app.Repository.OtpRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public void generateEmailOtp(String email) {

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        Otp otpEntity = new Otp();
        otpEntity.setEmail(email);
        otpEntity.setCode(otp);
        otpEntity.setType("EMAIL");
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpEntity.setAttempts(0);

        otpRepository.save(otpEntity);

        // 🔥 For now (testing)
        System.out.println("EMAIL OTP: " + otp);
    }
}
