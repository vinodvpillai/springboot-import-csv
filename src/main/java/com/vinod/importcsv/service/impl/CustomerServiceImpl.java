package com.vinod.importcsv.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.vinod.importcsv.dto.UploadCustomerRequest;
import com.vinod.importcsv.dto.UploadedCustomerResponse;
import com.vinod.importcsv.model.Customer;
import com.vinod.importcsv.repository.CustomerRepository;
import com.vinod.importcsv.service.ICustomerService;
import com.vinod.importcsv.util.GlobalUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.vinod.importcsv.util.ApplicationContant.*;

@Slf4j
@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    private static final List<String> CUSTOMER_HEADER=Arrays.asList(FIRST_NAME,LAST_NAME,EMAIL_ID,ADDRESS,MAX_CREDIT_LIMIT,CURRENT_CREDIT_LIMIT,STATUS);

    @Autowired
    private AmazonS3 amazonS3;
    /**
     * Add new customer.
     *
     * @param customer  - Customer object.
     * @return          - Persisted customer object.
     */
    @Override
    public Customer addCustomer(Customer customer) {
        log.trace("Request came to add new customer : {}",customer);
        customer.setStatus(true);
        Customer persistedCustomer=customerRepository.save(customer);
        log.trace("Successfully saved customer object and persisted object: {}",persistedCustomer);
        return persistedCustomer;
    }

    /**
     * Get customer object by customer id.
     *
     * @param id    - Customer ID.
     * @return      - Customer object.
     */
    @Override
    public Customer getCustomerById(Long id) {
        log.trace("Request came to fetch the customer having customer id : {}",id);
        Optional<Customer> optionalCustomer=customerRepository.findById(id);
        Customer customer=optionalCustomer.get();
        log.trace("Successfully fetched customer object : {} having customer id: {}",customer,id);
        return customer;
    }

    /**
     * Upload customer from the CSV file.
     *
     * @param uploadCustomerRequest - Request object.
     * @return                      - Uploaded customer response object.
     */
    @Override
    public UploadedCustomerResponse uploadCustomerDetailsFromCSV(UploadCustomerRequest uploadCustomerRequest) {
        log.trace("Request came to fetch the customer details from S3 bucket: {} file name : {}",uploadCustomerRequest.getBucketName(),uploadCustomerRequest.getFileName());
        UploadedCustomerResponse uploadedCustomerResponse=UploadedCustomerResponse.builder().fileName(uploadCustomerRequest.getFileName()).totalRecords(0).build();
        try{
            List<Customer> customerList=getContentFromS3Bucket(uploadCustomerRequest.getBucketName(),uploadCustomerRequest.getFileName());
            log.info("Total records fetched from the S3 bucket: {}, file name: {}, total size: {}",uploadCustomerRequest.getBucketName(),uploadCustomerRequest.getFileName(),customerList.size());
            List<Customer> persistedCustomerList=customerRepository.saveAll(customerList);
            log.info("Successfully saved total records :{} to the database",uploadCustomerRequest.getBucketName(),uploadCustomerRequest.getFileName(),persistedCustomerList.size());
            uploadedCustomerResponse.setTotalRecords(persistedCustomerList.size());
        } catch(Exception e) {
            log.warn("Error occurred while saving data from the CSV file error message : {}",e.getMessage(),e);
        }
        return uploadedCustomerResponse;
    }

    /**
     * Get the content from the S3 bucket and convert to the list of objects.
     *
     * @param bucketName    - Bucket name.
     * @param fileName      - File name.
     * @return              - List of customer objects.
     */
    private List<Customer> getContentFromS3Bucket(String bucketName, String fileName) {
        try{
            //Read from S3 bucket.
            S3ObjectInputStream s3ObjectInputStream=amazonS3.getObject(bucketName,fileName).getObjectContent();
            //InputStream inputStream=new FileInputStream(new ClassPathResource(fileName).getFile());
            List<Customer> resultCustomerList=readCustomerDetailsFromTheCSV(s3ObjectInputStream,fileName);
            return resultCustomerList;
        } catch(Exception e) {
            log.warn("Error occurred while reading the CSV file error message : {}",e.getMessage(),e);
        }
        return null;
    }


    /**
     * Read customer details from the CSV file.
     *
     * @param inputStream       - S3 Object Input stream reader object.
     * @param fileName          - File name.
     * @return                  - List of customer objects.
     */
    private List<Customer> readCustomerDetailsFromTheCSV(S3ObjectInputStream inputStream, String fileName){
        CSVParser csvParser=null;
        try{
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            csvParser=new CSVParser(bufferedReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            Map<String,String> errors=validateCSVFile(csvParser.getHeaderMap().keySet());
            if(!errors.isEmpty()){
                errors.keySet().stream().forEach(e -> log.warn("Error while fetching the customer details from CSV :{}",e));
                return null;
            }
            final List<CSVRecord> csvRecordList=csvParser.getRecords();
            int totalRecords=csvRecordList.size();
            log.info("Total records in the CSV file is: {}",totalRecords);
            List<Customer> customerList=csvRecordList.stream().map(csvRecord -> getCustomerObject(csvRecord)).collect(Collectors.toList());
            log.info("Total records convert from the CSV file to Customer object: {}",customerList.size());
            return customerList;
        } catch (Exception e) {
            log.warn("Error occurred while converting the CSV file to customer object error message : {}",e.getMessage(),e);
        }
        return null;
    }

    /**
     * Validate the CSV File header with the expected header.
     *
     * @param keySet    - Key Set details.
     * @return          - Map of error message.
     */
    private Map<String, String> validateCSVFile(Set<String> keySet) {
        Map<String,String> errors=new LinkedHashMap<>();
        for(String header:keySet) {
            if(!CUSTOMER_HEADER.contains(header)) {
                errors.put(header,header+ " Not defined");
            }
        }
        return errors;
    }

    /**
     * Convert the CSVRecord details to Customer object.
     *
     * @param csvRecord     - CSVRecord object.
     * @return              - Customer object.
     */
    private Customer getCustomerObject(final CSVRecord csvRecord) {
        final Customer customer= Customer.builder()
                .firstName(csvRecord.get(FIRST_NAME))
                .lastName(csvRecord.get(LAST_NAME))
                .emailId(csvRecord.get(EMAIL_ID))
                .address(csvRecord.get(ADDRESS))
                .maxCreditLimit(GlobalUtility.isNotNull(csvRecord.get(MAX_CREDIT_LIMIT))?new BigDecimal(csvRecord.get(MAX_CREDIT_LIMIT)):null)
                .currentCreditLimit(GlobalUtility.isNotNull(csvRecord.get(CURRENT_CREDIT_LIMIT))?new BigDecimal(csvRecord.get(CURRENT_CREDIT_LIMIT)):null).build();
        return customer;
    }
}
