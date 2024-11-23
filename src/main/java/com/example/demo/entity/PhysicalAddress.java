package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("physical_address")
public class PhysicalAddress {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String type; // 如 home, work

    private String address;
}