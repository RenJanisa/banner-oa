package com.banneroa.utils;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

public class AppJwtUtil {

    // TOKEN的有效期一小时（S）
    private static final int TOKEN_TIME_OUT = 3_600*12;
    // 加密KEY
    private static final String TOKEN_ENCRY_KEY = "@CopyrightByBanner";
    // 最小刷新间隔(S)
    private static final int REFRESH_TIME = 60_000;

    // 生产ID
    public static String getToken(Long id,Integer flag){
        Map<String, Object> claimMaps = new HashMap<>();
        claimMaps.put("id",id);
        claimMaps.put("flag",flag);
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(currentTime))  //签发时间
                .setSubject("system")  //说明
                .setIssuer("banner") //签发者信息
                .setAudience("app")  //接收用户
                .compressWith(CompressionCodecs.GZIP)  //数据压缩方式
                .signWith(SignatureAlgorithm.HS512, generalKey()) //加密方式
                .setExpiration(new Date(currentTime + TOKEN_TIME_OUT * 1000))  //过期时间戳
                .addClaims(claimMaps) //cla信息
                .compact();
    }

    /**
     * 获取token中的claims信息
     * @param token
     * @return
     */
    private static Jws<Claims> getJws(String token) {
            return Jwts.parser()
                    .setSigningKey(generalKey())
                    .parseClaimsJws(token);
    }

    /**
     * 获取payload body信息
     * @param token
     * @return
     */
    public static Claims getClaimsBody(String token) {
        try {
            return getJws(token).getBody();
        }catch (ExpiredJwtException e){
            return null;
        }
    }

    /**
     * 获取hearder body信息
     * @param token
     * @return
     */
    public static JwsHeader getHeaderBody(String token) {
        return getJws(token).getHeader();
    }

    /**
     * 是否过期
     * @param claims
     * @return 0: 过期, 1,2: 未过期
     */
    public static int checkTokenTime(Claims claims) {
        if(claims==null){
            return 0;
        }

        try {
            if (claims.getExpiration().after(new Date())){
                return 1;
            }
            System.out.println(System.currentTimeMillis()-claims.getExpiration().getTime());
            if(System.currentTimeMillis()-claims.getExpiration().getTime()<REFRESH_TIME){
                return 2;
            }
            return 0;
        }catch (Exception e){
            //时间戳过期报错
            return 0;
        }
    }

    /**
     * 由字符串生成加密key
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(TOKEN_ENCRY_KEY.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

}
