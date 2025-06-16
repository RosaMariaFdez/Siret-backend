package dqteam.siret.security;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Base64;

@Service
public class TwoFactorAuthService {
    private static final String ISSUER = "Siret"; 

    public String generateSecretKey() {
        byte[] buffer = new byte[20];
        new java.security.SecureRandom().nextBytes(buffer);
        return Base64.getEncoder().withoutPadding().encodeToString(buffer);
    }

    public String getGoogleAuthenticatorQR(String email, String secret) {
        String otpAuthUrl = "otpauth://totp/" + ISSUER + ":" + email + "?secret=" + secret + "&issuer=" + ISSUER;
        return generateQRCode(otpAuthUrl);
    }

    private String generateQRCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generando QR", e);
        }
    }
}
