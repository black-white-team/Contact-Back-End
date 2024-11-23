package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("bookmark")
@Data
public class BookMark {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private Integer phonenumber;
    private String emailaddress;
    private String socialmediahandles;
    private String physicaladdress;
}