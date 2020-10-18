package com.vinod.importcsv.service;


import com.vinod.importcsv.dto.UploadCustomerRequest;
import com.vinod.importcsv.dto.UploadedCustomerResponse;
import com.vinod.importcsv.model.Customer;

public interface ICustomerService {

    /**
     * Add new customer.
     *
     * @param customer  - Customer object.
     * @return          - Persisted customer object.
     */
    Customer addCustomer(Customer customer);

    /**
     * Get customer object by customer id.
     *
     * @param id    - Customer ID.
     * @return      - Customer object.
     */
    Customer getCustomerById(Long id);

    /**
     * Upload customer datails from CSV.
     *
     * @param uploadCustomerRequest - Request object.
     * @return                      - Response object.
     */
    UploadedCustomerResponse uploadCustomerDetailsFromCSV(UploadCustomerRequest uploadCustomerRequest);
}
