package com.examples.carcrud.config;

import com.examples.carcrud.model.AppVisitCounter;
import com.examples.carcrud.model.RequestTimer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration that registers an interceptor to populate
 * request-scoped and application-scoped data into every page model.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestTimer requestTimer;
    private final AppVisitCounter appVisitCounter;

    public WebConfig(RequestTimer requestTimer, AppVisitCounter appVisitCounter) {
        this.requestTimer = requestTimer;
        this.appVisitCounter = appVisitCounter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                // Application scope: increment global visit counter on every page request
                appVisitCounter.increment();
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response,
                                   Object handler, ModelAndView modelAndView) {
                if (modelAndView != null && modelAndView.hasView()) {
                    // Request scope: add the elapsed time for this specific request
                    modelAndView.addObject("requestTimeMs", requestTimer.getElapsedMs());
                    // Application scope: add the global visit count
                    modelAndView.addObject("totalVisits", appVisitCounter.getCount());
                }
            }
        });
    }
}
