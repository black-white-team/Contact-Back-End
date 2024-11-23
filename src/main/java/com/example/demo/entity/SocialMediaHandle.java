package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("social_media_handle")
public class SocialMediaHandle {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String platform; // 如 Twitter, Facebook

    private String handle;
}
