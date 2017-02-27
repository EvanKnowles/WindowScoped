package za.co.knonchalant.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CookieFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);

        Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        for (Cookie cookie : cookies) {
            if ("windowId".equals(cookie.getName())) {
                cookie.setMaxAge(0);
                httpServletResponse.addCookie(cookie);
            }
        }
        httpServletResponse.addHeader("Cache-Control", "no-cache");
        httpServletResponse.addHeader("Pragma", "no-cache");
    }

    @Override
    public void destroy() {

    }
}
