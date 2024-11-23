package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("phone_number")
public class PhoneNumber {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String type; // 如 mobile, home, work

    private String number;
}
