// UserExcel.java
package com.example.demo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserExcel {
    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("Username")
    private String username;

    @ExcelProperty("Bookmark")
    private Boolean bookmark;

    @ExcelProperty("Phone Numbers")
    private String phoneNumbers;

    @ExcelProperty("Email Addresses")
    private String emailAddresses;

    @ExcelProperty("Social Media Handles")
    private String socialMediaHandles;

    @ExcelProperty("Physical Addresses")
    private String physicalAddresses;
}