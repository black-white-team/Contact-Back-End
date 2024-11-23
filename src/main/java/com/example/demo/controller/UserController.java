package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserMapper userMapper;

    @Resource
    PhoneNumberMapper phoneNumberMapper;

    @Resource
    EmailAddressMapper emailAddressMapper;

    @Resource
    SocialMediaHandleMapper socialMediaHandleMapper;

    @Resource
    PhysicalAddressMapper physicalAddressMapper;

    // 添加用户及其联系方式
    @PostMapping
    @Transactional
    public Result<?> save(@RequestBody UserDTO userDTO) {
        // 创建用户
        User user = new User();
        user.setUsername(userDTO.getUsername());
        userMapper.insert(user);

        // 获取生成的用户ID
        Long userId = user.getId();

        // 插入电话号码
        if (userDTO.getPhoneNumbers() != null) {
            for (PhoneNumber phone : userDTO.getPhoneNumbers()) {
                phone.setUserId(userId);
                phoneNumberMapper.insert(phone);
            }
        }

        // 插入电子邮件地址
        if (userDTO.getEmailAddresses() != null) {
            for (EmailAddress email : userDTO.getEmailAddresses()) {
                email.setUserId(userId);
                emailAddressMapper.insert(email);
            }
        }

        // 插入社交媒体账户
        if (userDTO.getSocialMediaHandles() != null) {
            for (SocialMediaHandle social : userDTO.getSocialMediaHandles()) {
                social.setUserId(userId);
                socialMediaHandleMapper.insert(social);
            }
        }

        // 插入物理地址
        if (userDTO.getPhysicalAddresses() != null) {
            for (PhysicalAddress address : userDTO.getPhysicalAddresses()) {
                address.setUserId(userId);
                physicalAddressMapper.insert(address);
            }
        }

        return Result.success();
    }

    // 更新用户及其联系方式
    @PutMapping("/{id}")
    @Transactional
    public Result<?> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        // 更新用户基本信息
        User user = new User();
        user.setId(id);
        user.setUsername(userDTO.getUsername());
        userMapper.updateById(user);

        // 删除旧的联系方式
        phoneNumberMapper.delete(new LambdaQueryWrapper<PhoneNumber>().eq(PhoneNumber::getUserId, id));
        emailAddressMapper.delete(new LambdaQueryWrapper<EmailAddress>().eq(EmailAddress::getUserId, id));
        socialMediaHandleMapper.delete(new LambdaQueryWrapper<SocialMediaHandle>().eq(SocialMediaHandle::getUserId, id));
        physicalAddressMapper.delete(new LambdaQueryWrapper<PhysicalAddress>().eq(PhysicalAddress::getUserId, id));

        // 插入新的电话号码
        if (userDTO.getPhoneNumbers() != null) {
            for (PhoneNumber phone : userDTO.getPhoneNumbers()) {
                phone.setUserId(id);
                phoneNumberMapper.insert(phone);
            }
        }

        // 插入新的电子邮件地址
        if (userDTO.getEmailAddresses() != null) {
            for (EmailAddress email : userDTO.getEmailAddresses()) {
                email.setUserId(id);
                emailAddressMapper.insert(email);
            }
        }

        // 插入新的社交媒体账户
        if (userDTO.getSocialMediaHandles() != null) {
            for (SocialMediaHandle social : userDTO.getSocialMediaHandles()) {
                social.setUserId(id);
                socialMediaHandleMapper.insert(social);
            }
        }

        // 插入新的物理地址
        if (userDTO.getPhysicalAddresses() != null) {
            for (PhysicalAddress address : userDTO.getPhysicalAddresses()) {
                address.setUserId(id);
                physicalAddressMapper.insert(address);
            }
        }

        return Result.success();
    }

    // 分页查询用户及其联系方式
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(User::getUsername, search)
                    .or()
                    .like(User::getId, search); // 根据ID搜索
        }
        Page<User> userPage = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 构建 UserDTO 列表
        List<UserDTO> userDTOList = userPage.getRecords().stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());

            // 查询并设置电话号码
            List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(
                    new LambdaQueryWrapper<PhoneNumber>().eq(PhoneNumber::getUserId, user.getId()));
            dto.setPhoneNumbers(phoneNumbers);

            // 查询并设置电子邮件地址
            List<EmailAddress> emailAddresses = emailAddressMapper.selectList(
                    new LambdaQueryWrapper<EmailAddress>().eq(EmailAddress::getUserId, user.getId()));
            dto.setEmailAddresses(emailAddresses);

            // 查询并设置社交媒体账户
            List<SocialMediaHandle> socialMediaHandles = socialMediaHandleMapper.selectList(
                    new LambdaQueryWrapper<SocialMediaHandle>().eq(SocialMediaHandle::getUserId, user.getId()));
            dto.setSocialMediaHandles(socialMediaHandles);

            // 查询并设置物理地址
            List<PhysicalAddress> physicalAddresses = physicalAddressMapper.selectList(
                    new LambdaQueryWrapper<PhysicalAddress>().eq(PhysicalAddress::getUserId, user.getId()));
            dto.setPhysicalAddresses(physicalAddresses);

            return dto;
        }).toList();

        // 构建分页结果
        Page<UserDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(userPage.getCurrent());
        dtoPage.setSize(userPage.getSize());
        dtoPage.setTotal(userPage.getTotal());
        dtoPage.setRecords(userDTOList);

        return Result.success(dtoPage);
    }

    // 导出用户数据到 Excel 文件（包含联系方式）
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<UserDTO> list = userMapper.selectList(null).stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());

            // 设置电话号码
            List<PhoneNumber> phoneNumbers = phoneNumberMapper.selectList(
                    new LambdaQueryWrapper<PhoneNumber>().eq(PhoneNumber::getUserId, user.getId()));
            dto.setPhoneNumbers(phoneNumbers);

            // 设置电子邮件地址
            List<EmailAddress> emailAddresses = emailAddressMapper.selectList(
                    new LambdaQueryWrapper<EmailAddress>().eq(EmailAddress::getUserId, user.getId()));
            dto.setEmailAddresses(emailAddresses);

            // 设置社交媒体账户
            List<SocialMediaHandle> socialMediaHandles = socialMediaHandleMapper.selectList(
                    new LambdaQueryWrapper<SocialMediaHandle>().eq(SocialMediaHandle::getUserId, user.getId()));
            dto.setSocialMediaHandles(socialMediaHandles);

            // 设置物理地址
            List<PhysicalAddress> physicalAddresses = physicalAddressMapper.selectList(
                    new LambdaQueryWrapper<PhysicalAddress>().eq(PhysicalAddress::getUserId, user.getId()));
            dto.setPhysicalAddresses(physicalAddresses);

            return dto;
        }).toList();

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("users_with_contacts", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 写入Excel
        EasyExcel.write(response.getOutputStream(), UserDTO.class)
                .sheet("用户信息")
                .doWrite(list);
    }

    // 从 Excel 文件导入用户数据（包含联系方式）
    @PostMapping("/import")
    @Transactional
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        List<UserDTO> list = EasyExcel.read(file.getInputStream())
                .head(UserDTO.class)
                .sheet()
                .doReadSync();

        for (UserDTO dto : list) {
            // 创建用户
            User user = new User();
            user.setUsername(dto.getUsername());
            userMapper.insert(user);
            Long userId = user.getId();

            // 插入电话号码
            if (dto.getPhoneNumbers() != null) {
                for (PhoneNumber phone : dto.getPhoneNumbers()) {
                    phone.setUserId(userId);
                    phoneNumberMapper.insert(phone);
                }
            }

            // 插入电子邮件地址
            if (dto.getEmailAddresses() != null) {
                for (EmailAddress email : dto.getEmailAddresses()) {
                    email.setUserId(userId);
                    emailAddressMapper.insert(email);
                }
            }

            // 插入社交媒体账户
            if (dto.getSocialMediaHandles() != null) {
                for (SocialMediaHandle social : dto.getSocialMediaHandles()) {
                    social.setUserId(userId);
                    socialMediaHandleMapper.insert(social);
                }
            }

            // 插入物理地址
            if (dto.getPhysicalAddresses() != null) {
                for (PhysicalAddress address : dto.getPhysicalAddresses()) {
                    address.setUserId(userId);
                    physicalAddressMapper.insert(address);
                }
            }
        }

        return Result.success();
    }
}