package de.jawb.keysafe.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.jawb.keysafe.backend.api.ApiProfileBackupRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JsonParserTests {

    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2022, Month.APRIL,14,16,33,21);
    private static final String DATE_AS_STRING   = "14.04.2022, 16:33:21";

    @Inject
    private ObjectMapper mapper;

    @Test
    public void testDateFormat() throws Exception {
        String json = mapper.writeValueAsString(DATE_TIME);
        Assertions.assertEquals(DATE_AS_STRING, json.replace("" + '"', ""));
    }

    @Test
    public void testDateSerialization() throws Exception {

        ApiProfileBackupRequest profile = new ApiProfileBackupRequest(
                "profile",
                "data",
                "checksum",
                DATE_TIME
        );

        final String json = mapper.writeValueAsString(profile);

        {
            Map<?, ?> fromJson = mapper.readValue(json, Map.class);
            Assertions.assertEquals(DATE_AS_STRING, fromJson.get("dataDate"));
        }

        {
            ApiProfileBackupRequest fromJson = mapper.readValue(json, ApiProfileBackupRequest.class);
            Assertions.assertEquals(profile, fromJson);
        }
    }
}
