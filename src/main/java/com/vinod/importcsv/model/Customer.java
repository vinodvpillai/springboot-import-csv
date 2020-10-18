package com.vinod.importcsv.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Entity
@Table(name="customer",schema = "csv-customer")
public class Customer implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String emailId;
    @Column
    private String address;
    @Column
    private BigDecimal maxCreditLimit;
    @Column
    private BigDecimal currentCreditLimit;
    @Column
    private Boolean status;
}
