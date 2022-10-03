package de.jawb.keysafe.backend.app.endpoint.interceptor;

import de.jawb.tools.date.DateUtil;
import de.jawb.tools.date.DurationStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DurationLogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(DurationLogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("timestamp", System.currentTimeMillis());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        Long timestamp  = (Long)request.getAttribute("timestamp");
        String duration = DateUtil.toReadableDuration(System.currentTimeMillis() - timestamp, DurationStyle.HumanReadable);

        logger.info("{} {} in {}", request.getMethod(), request.getRequestURI(), duration);
    }

}
