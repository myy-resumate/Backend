package dev.resumate.common.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    //request에서 쿠키 추출
    public String getCookie(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;  //쿠키에 해당 이름의 값이 없음.
    }

    //response에 쿠키 추가
    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");  //쿠키를 모든 경로에서 사용 가능
        cookie.setMaxAge(maxAge);  //쿠키 만료기간
        cookie.setHttpOnly(true);  //HTTP Only로 설정
        response.addCookie(cookie);
    }
}
