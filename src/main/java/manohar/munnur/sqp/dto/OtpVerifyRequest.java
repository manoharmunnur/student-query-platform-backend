package manohar.munnur.sqp.dto;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String userId;
    private String emailOtp;
//    private String mobileOtp;
}
