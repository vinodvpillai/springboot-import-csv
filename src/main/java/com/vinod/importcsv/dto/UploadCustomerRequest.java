package com.vinod.importcsv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadCustomerRequest {
    private String bucketName;
    private String fileName;
}
