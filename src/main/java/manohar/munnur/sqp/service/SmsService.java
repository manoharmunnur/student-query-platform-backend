package manohar.munnur.sqp.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//@Service
public class SmsService {

//    @Value("${fast2sms.api.key}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public void sendOtpSms(String phoneNumber, String otp) {
//        try {
//            String message = "Your OTP is: " + otp + " (valid for 5 minutes)";
//            // URL encode message
//            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
//
//            String url = "https://www.fast2sms.com/dev/bulkV2?route=v3&sender_id=TXTIND&message="
//                    + encodedMessage + "&language=english&numbers=" + phoneNumber;
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("authorization", apiKey); // required by Fast2SMS
//            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    new URI(url),
//                    HttpMethod.GET,
//                    entity,
//                    String.class
//            );
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                System.out.println("SMS sent, response: " + response.getBody());
//            } else {
//                System.err.println("SMS send failed, status: " + response.getStatusCode() + " body: " + response.getBody());
//            }
//
//        } catch (Exception e) {
//            System.err.println("SMS sending failed: " + e.getMessage());
//            // log stacktrace in real app
//        }
//    }
}
