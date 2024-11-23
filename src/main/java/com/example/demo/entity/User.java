package com.example.demo.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class User {
    @TableId(type = IdType.AUTO)
    @ExcelProperty("ID")
    private Integer id;

    @ExcelProperty("Username")
    private String username;

    @ExcelProperty("Phone Number")
    private String phonenumber;

    @ExcelProperty("Email Address")
    private String emailaddress;

    @ExcelProperty("Social Media Handles")
    private String socialmediahandles;

    @ExcelProperty("Physical Address")
    private String physicaladdresses;

    @ExcelProperty("Bookmarks")
    private Boolean bookmarks;
}