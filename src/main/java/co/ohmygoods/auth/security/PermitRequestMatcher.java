package co.ohmygoods.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

public class PermitRequestMatcher {

    private final List<RequestMatcher> requestMatchers;

    public PermitRequestMatcher(List<String> patterns) {
        this.requestMatchers = new ArrayList<>();
        patterns.forEach(pattern -> requestMatchers.add(AntPathRequestMatcher.antMatcher(pattern)));
    }

    public void add(String pattern) {
        this.requestMatchers.add(AntPathRequestMatcher.antMatcher(pattern));
    }

    public boolean matches(HttpServletRequest request) {
        return requestMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
