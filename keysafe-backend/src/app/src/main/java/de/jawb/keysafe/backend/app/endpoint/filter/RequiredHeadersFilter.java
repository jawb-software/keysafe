package de.jawb.keysafe.backend.app.endpoint.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.jawb.keysafe.backend.api.ApiErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class RequiredHeadersFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequiredHeadersFilter.class);

    private static final ObjectMapper OBJECT_MAPPER   = new ObjectMapper();

    private static final Set<String> REQUIRED_HEADERS = new HashSet<>(Arrays.asList(
            "User-Agent"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        logger.info("filter: {} {}", request.getMethod(), request.getRequestURI());

        for(String headerName : REQUIRED_HEADERS){
            String header = request.getHeader(headerName);
//            logger.info("header: {} -> {}", headerName, header);

            if(header == null){

                ApiErrorInfo errorInfo = new ApiErrorInfo(
                        400,
                        "Bad Request",
                        "Missing header: " + headerName
                );

                String serialized = OBJECT_MAPPER.writeValueAsString(errorInfo);

                response.setHeader("Content-Type", "application/json");
                response.setStatus(400);
                response.getOutputStream().write(serialized.getBytes());

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}