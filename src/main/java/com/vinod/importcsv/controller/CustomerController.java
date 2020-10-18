package com.vinod.importcsv.controller;

import com.vinod.importcsv.dto.UploadCustomerRequest;
import com.vinod.importcsv.dto.UploadedCustomerResponse;
import com.vinod.importcsv.model.Customer;
import com.vinod.importcsv.service.ICustomerService;
import com.vinod.importcsv.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.vinod.importcsv.util.GlobalUtility.buildResponseForError;
import static com.vinod.importcsv.util.GlobalUtility.buildResponseForSuccess;

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {
    @Autowired
    private ICustomerService customerService;

    @PostMapping
    public ResponseEntity<Response> addNewCustomer(@RequestBody Customer customer) {
        try {
            log.trace("Request came to add new customer with following details: {}", customer);
            Customer persistedCustomer = customerService.addCustomer(customer);
            return buildResponseForSuccess(HttpStatus.SC_OK, "Successfully added new customer", persistedCustomer);
        } catch (Exception e) {
            log.error("Unable to create the customer for email id: {}, error msg: {}", customer.getEmailId(), e.getMessage(), e);
            return buildResponseForError(HttpStatus.SC_INTERNAL_SERVER_ERROR, String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), "Oops! Something went wrong.", null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getCustomer(@PathVariable("id") Long id) {
        try {
            log.trace("Request came to get the customer details having the id: {}", id);
            Customer customer = customerService.getCustomerById(id);
            return buildResponseForSuccess(HttpStatus.SC_OK, "Successfully fetched customer", customer);
        } catch (Exception e) {
            log.error("Unable to get the customer details for id: {}, error msg: {}", id, e.getMessage(), e);
            return buildResponseForError(HttpStatus.SC_INTERNAL_SERVER_ERROR, String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), "Oops! Something went wrong.", null);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Response> uploadCustomerDetails(@RequestBody UploadCustomerRequest uploadCustomerRequest) {
        try {
            log.trace("Request came to add customer details from S3 bucket: {}, file name: {}", uploadCustomerRequest.getBucketName(), uploadCustomerRequest.getFileName());
            UploadedCustomerResponse uploadedCustomerResponse=customerService.uploadCustomerDetailsFromCSV(uploadCustomerRequest);
            return buildResponseForSuccess(HttpStatus.SC_OK, "Successfully uploaded customer details", uploadedCustomerResponse);
        } catch (Exception e) {
            log.error("Unable to upload the customer details from S3 bucket: {}, file name: {}, error message: {}", uploadCustomerRequest.getBucketName(),uploadCustomerRequest.getFileName(), e.getMessage(), e);
            return buildResponseForError(HttpStatus.SC_INTERNAL_SERVER_ERROR, String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), "Oops! Something went wrong.", null);
        }
    }
}
