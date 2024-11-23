// UserController.java
package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import com.example.demo.excel.UserExcel;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

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
        user.setBookmark(false); // 默认设置为未收藏
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
        user.setBookmark(userDTO.getBookmark()); // 更新收藏状态
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

    // 删除用户及其所有关联联系方式
    @DeleteMapping("/{id}")
    @Transactional
    public Result<?> delete(@PathVariable Long id) {
        // 删除用户基本信息
        userMapper.deleteById(id);

        // 删除用户的所有联系方式
        phoneNumberMapper.delete(new LambdaQueryWrapper<PhoneNumber>().eq(PhoneNumber::getUserId, id));
        emailAddressMapper.delete(new LambdaQueryWrapper<EmailAddress>().eq(EmailAddress::getUserId, id));
        socialMediaHandleMapper.delete(new LambdaQueryWrapper<SocialMediaHandle>().eq(SocialMediaHandle::getUserId, id));
        physicalAddressMapper.delete(new LambdaQueryWrapper<PhysicalAddress>().eq(PhysicalAddress::getUserId, id));

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
            dto.setBookmark(user.getBookmark());

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
        }).collect(Collectors.toList());

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
            dto.setBookmark(user.getBookmark());

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
        }).collect(Collectors.toList());

        // 转换为 UserExcel
        List<UserExcel> excelList = list.stream().map(dto -> {
            UserExcel excel = new UserExcel();
            excel.setId(dto.getId());
            excel.setUsername(dto.getUsername());
            excel.setBookmark(dto.getBookmark()); // 添加收藏状态到Excel

            excel.setPhoneNumbers(dto.getPhoneNumbers().stream()
                    .map(p -> p.getType() + ": " + p.getNumber())
                    .collect(Collectors.joining(", ")));
            excel.setEmailAddresses(dto.getEmailAddresses().stream()
                    .map(e -> e.getType() + ": " + e.getEmail())
                    .collect(Collectors.joining(", ")));
            excel.setSocialMediaHandles(dto.getSocialMediaHandles().stream()
                    .map(s -> s.getPlatform() + ": " + s.getHandle())
                    .collect(Collectors.joining(", ")));
            excel.setPhysicalAddresses(dto.getPhysicalAddresses().stream()
                    .map(a -> a.getType() + ": " + a.getAddress())
                    .collect(Collectors.joining(", ")));
            return excel;
        }).collect(Collectors.toList());

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("users_with_contacts", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 写入Excel
        EasyExcel.write(response.getOutputStream(), UserExcel.class)
                .sheet("用户信息")
                .doWrite(excelList);
    }

    // 从 Excel 文件导入用户数据（包含联系方式）
    @PostMapping("/import")
    @Transactional
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        List<UserExcel> excelList = EasyExcel.read(file.getInputStream())
                .head(UserExcel.class)
                .sheet()
                .doReadSync();

        for (UserExcel excel : excelList) {
            // 创建用户
            User user = new User();
            user.setUsername(excel.getUsername());
            user.setBookmark(excel.getBookmark()); // 设置收藏状态
            userMapper.insert(user);
            Long userId = user.getId();

            // 解析并插入电话号码
            if (StrUtil.isNotBlank(excel.getPhoneNumbers())) {
                String[] phones = excel.getPhoneNumbers().split(", ");
                for (String phoneStr : phones) {
                    String[] parts = phoneStr.split(": ");
                    if (parts.length == 2) {
                        PhoneNumber phone = new PhoneNumber();
                        phone.setUserId(userId);
                        phone.setType(parts[0]);
                        phone.setNumber(parts[1]);
                        phoneNumberMapper.insert(phone);
                    }
                }
            }

            // 解析并插入电子邮件地址
            if (StrUtil.isNotBlank(excel.getEmailAddresses())) {
                String[] emails = excel.getEmailAddresses().split(", ");
                for (String emailStr : emails) {
                    String[] parts = emailStr.split(": ");
                    if (parts.length == 2) {
                        EmailAddress email = new EmailAddress();
                        email.setUserId(userId);
                        email.setType(parts[0]);
                        email.setEmail(parts[1]);
                        emailAddressMapper.insert(email);
                    }
                }
            }

            // 解析并插入社交媒体账户
            if (StrUtil.isNotBlank(excel.getSocialMediaHandles())) {
                String[] socials = excel.getSocialMediaHandles().split(", ");
                for (String socialStr : socials) {
                    String[] parts = socialStr.split(": ");
                    if (parts.length == 2) {
                        SocialMediaHandle social = new SocialMediaHandle();
                        social.setUserId(userId);
                        social.setPlatform(parts[0]);
                        social.setHandle(parts[1]);
                        socialMediaHandleMapper.insert(social);
                    }
                }
            }

            // 解析并插入物理地址
            if (StrUtil.isNotBlank(excel.getPhysicalAddresses())) {
                String[] addresses = excel.getPhysicalAddresses().split(", ");
                for (String addressStr : addresses) {
                    String[] parts = addressStr.split(": ");
                    if (parts.length == 2) {
                        PhysicalAddress address = new PhysicalAddress();
                        address.setUserId(userId);
                        address.setType(parts[0]);
                        address.setAddress(parts[1]);
                        physicalAddressMapper.insert(address);
                    }
                }
            }
        }

        return Result.success();
    }

    // 切换收藏状态
    @PatchMapping("/favorite/{id}")
    @Transactional
    public Result<?> toggleFavorite(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("400", "User not found");
        }
        user.setBookmark(!user.getBookmark()); // 切换收藏状态
        userMapper.updateById(user);
        return Result.success();
    }

    // 获取收藏的用户
    @GetMapping("/favorites")
    public Result<?> getFavorites(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getBookmark, true); // 过滤 bookmark 字段为 true 的用户
        Page<User> favoritePage = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 构建 UserDTO 列表
        List<UserDTO> userDTOList = favoritePage.getRecords().stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setBookmark(user.getBookmark());

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
        }).collect(Collectors.toList());

        // 构建分页结果
        Page<UserDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(favoritePage.getCurrent());
        dtoPage.setSize(favoritePage.getSize());
        dtoPage.setTotal(favoritePage.getTotal());
        dtoPage.setRecords(userDTOList);

        return Result.success(dtoPage);
    }
}