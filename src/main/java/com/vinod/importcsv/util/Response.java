package com.vinod.importcsv.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Builder
@Log4j2
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private int status;
    private String message;
    private Object data;
    private String errorStatus;
    private String errorReason;
    private Object errorCause;
}
