package de.jawb.keysafe.backend.app.endpoint.resolvers;

import de.jawb.keysafe.backend.api.ApiCallerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class ApiCallerInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger logger = LoggerFactory.getLogger(ApiCallerInfoArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ApiCallerInfo.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        logger.debug("resolveArgument: {}", parameter.getParameterType().getSimpleName());

        HttpServletRequest req = (HttpServletRequest) request.getNativeRequest();

        String ip     = getIP(req);
        String callId = getUserAgent(req);
        String userId = req.getHeader("User-Id");

        return new ApiCallerInfo(callId, userId, ip);

    }

    private String getUserAgent(HttpServletRequest req) {
        return req.getHeader("User-Agent");
    }

    private String getIP(HttpServletRequest req) {
        String ip = req.getHeader("x-forwarded-for");

        if(ip == null){
            ip = req.getRemoteAddr();
        }
        return ip;
    }

}