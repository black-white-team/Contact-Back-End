package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
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

    // 添加用户
    @PostMapping
    public Result<?> save(@RequestBody User user) {
        user.setBookmarks(false); // 默认设置为未收藏
        userMapper.insert(user);
        return Result.success();
    }

    // 更新用户
    @PutMapping
    public Result<?> update(@RequestBody User user) {
        userMapper.updateById(user);
        return Result.success();
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.success();
    }

    // 分页查询用户
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(User::getUsername, search)
                    .or()
                    .like(User::getPhonenumber, search)
                    .or()
                    .like(User::getEmailaddress, search);
        }
        Page<User> userPage = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(userPage);
    }

    // 切换收藏状态
    @PatchMapping("/favorite/{id}")
    public Result<?> toggleFavorite(@PathVariable Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("400", "User not found");
        }
        user.setBookmarks(!user.getBookmarks()); // 切换收藏状态
        userMapper.updateById(user);
        return Result.success();
    }

    // 获取收藏的用户
    @GetMapping("/favorites")
    public Result<?> getFavorites(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getBookmarks, true); // 确保这里过滤 bookmarks 字段为 true 的用户
        Page<User> favoritePage = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(favoritePage.getRecords(), favoritePage.getTotal());
    }

    // 导出用户数据到 Excel 文件
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<User> list = userMapper.selectList(null);

        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("users", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 写入Excel
        EasyExcel.write(response.getOutputStream(), User.class).sheet("用户信息").doWrite(list);
    }

    // 从 Excel 文件导入用户数据
    @PostMapping("/import")
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        List<User> list = EasyExcel.read(file.getInputStream())
                .head(User.class)
                .sheet()
                .doReadSync();

        for (User user : list) {
            userMapper.insert(user);
        }
        return Result.success();
    }
}