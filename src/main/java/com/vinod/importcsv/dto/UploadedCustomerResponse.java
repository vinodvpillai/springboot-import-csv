package com.vinod.importcsv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedCustomerResponse {
    private String fileName;
    private Integer totalRecords;
}
