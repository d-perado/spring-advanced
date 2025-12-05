package org.example.expert.config;

import io.jsonwebtoken.Claims;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtUtilTest {

    JwtUtil jwtUtil = new JwtUtil();

    @BeforeEach
    void setUp(){
        String secretKey = "zoc94IisYccfxWd5NX9tfJ2PF43mNTg27VdgKh2vp86";

        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);

        jwtUtil.init();
    }

    @Test
    void JWT생성() {
        //given
        long id = 1L;
        String email = "email";
        UserRole userRole = UserRole.USER;
        //when
        String result = jwtUtil.createToken(id, email, userRole);
        //then
        assertThat(result).startsWith("Bearer ");

    }

    @Test
    void 토큰_Bearer_자르기_성공() {
        //given
        String token = "Bearer xxxx.yyyy.zzzz";
        //when
        String result = jwtUtil.substringToken(token);
        //then
        assertThat(result).isEqualTo("xxxx.yyyy.zzzz");
    }

    @Test
    void 토큰_Bearer_자르기_실패() {
        //given
        String token = "Bear";
        //when
        assertThatThrownBy(() -> jwtUtil.substringToken(token))
                //then
                .isInstanceOf(ServerException.class);
    }

    @Test
    void Claim추출_성공() {
        //given
        String token = jwtUtil.createToken(1L, "email@email.com", UserRole.ADMIN);
        String pure = jwtUtil.substringToken(token);
        //when
        Claims claims = jwtUtil.extractClaims(pure);
        //then
        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("email", String.class)).isEqualTo("email@email.com");
        assertThat(claims.get("userRole", String.class)).isEqualTo("ADMIN");
    }
}
