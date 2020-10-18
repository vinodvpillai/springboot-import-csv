package com.vinod.importcsv.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Log4j2
public class GlobalUtility {

    /**
     * Response for success.
     *
     * @param statusCode
     * @param message
     * @param responseData
     * @return
     */
    public static ResponseEntity<Response> buildResponseForSuccess(final int statusCode, final String message, final Object responseData) {
        Response apiResponse = Response.builder().status(statusCode).message(message).data(responseData).build();
        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * Response for Failed.
     *
     * @param statusCode
     * @param errorStatus
     * @param errorReason
     * @param errorCause
     * @return
     */
    public static ResponseEntity<Response> buildResponseForError(final int statusCode, final String errorStatus,final String errorReason, final Object errorCause) {
        Response apiResponse = Response.builder().status(statusCode).errorStatus(errorStatus).errorReason(errorReason).errorCause(errorCause).build();
        return ResponseEntity.status(statusCode).body(apiResponse);
    }

    /**
     * Convert object to json.
     *
     * @param input     - Input object.
     * @return          - JSON string object.
     */
    public static String convertObjectToJson(final Object input) {
        try {
            JavaTimeModule javaTimeModule=new JavaTimeModule();
            javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
            ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modules(javaTimeModule).featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();
            objectMapper.setSerializationInclusion(Include.NON_NULL);
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            log.error("Error generated while converting object to JSON error msg: {}", ExceptionUtils.getRootCauseMessage(e));
        }
        return null;
    }

    public static ObjectMapper getDateFormatObjectMapper() {
        try {
            JavaTimeModule javaTimeModule=new JavaTimeModule();
            javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
            return Jackson2ObjectMapperBuilder.json().modules(javaTimeModule).featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();
        } catch (Exception e) {
            log.error("Error generated while creating object for object mapper for date format: {}", ExceptionUtils.getRootCauseMessage(e));
        }
        return null;
    }

    public static boolean isNotNull(Object object) {
        return object!=null && !"".equals(object);
    }
}
