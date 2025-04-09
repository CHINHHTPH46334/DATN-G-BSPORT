package com.example.gbsports.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;

public class HMACUtil {
    public final static String HMACMD5 = "HmacMD5";
    public final static String HMACSHA1 = "HmacSHA1";
    public final static String HMACSHA256 = "HmacSHA256";
    public final static String HMACSHA512 = "HmacSHA512";
    public final static Charset UTF8CHARSET = Charset.forName("UTF-8");

    public final static LinkedList<String> HMACS = new LinkedList<String>(Arrays.asList("UnSupport", "HmacSHA256", "HmacMD5", "HmacSHA384", "HMacSHA1", "HmacSHA512"));

    public static String calculateHmac(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        // Chọn thuật toán HMAC SHA-256
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);

        // Tạo key từ chuỗi key
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
        mac.init(secretKeySpec);

        // Tính toán HMAC
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Chuyển đổi kết quả thành chuỗi hex
        return bytesToHex(hmacBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] HMacEncode(final String algorithm, final String key, final String data) {
        Mac macGenerator = null;
        try {
            macGenerator = Mac.getInstance(algorithm);
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
            macGenerator.init(signingKey);
        } catch (Exception ex) {
        }

        if (macGenerator == null) {
            return null;
        }

        byte[] dataByte = null;
        try {
            dataByte = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        return macGenerator.doFinal(dataByte);
    }

    public static String HMacBase64Encode(final String algorithm, final String key, final String data) {
        byte[] hmacEncodeBytes = HMacEncode(algorithm, key, data);
        if (hmacEncodeBytes == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(hmacEncodeBytes);
    }

    public static String HMacHexStringEncode(final String algorithm, final String key, final String data) {
        byte[] hmacEncodeBytes = HMacEncode(algorithm, key, data);
        if (hmacEncodeBytes == null) {
            return null;
        }
        return HexStringUtil.byteArrayToHexString(hmacEncodeBytes);
    }

    public static String hmacSha256Hex(String secretKey, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        return Hex.encodeHexString(hash);
    }

}
