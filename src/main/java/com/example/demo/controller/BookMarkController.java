package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.BookMark;
import com.example.demo.mapper.BookMarkMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

//用于记录数据
@RestController
//统一路由
@RequestMapping("/bookmark")
public class BookMarkController {
    @Resource
    BookMarkMapper bookMapper;

    //    前台传送用户数据 在这个接口接收到并转成user类
    @PostMapping
    public Result<?> save(@RequestBody BookMark bookMark) {
        bookMapper.insert(bookMark);
        return Result.success();
    }

    //    更新数据
    @PutMapping
    public Result<?> update(@RequestBody BookMark bookMark) {
        bookMapper.updateById(bookMark);
        return Result.success();
    }

    //    删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bookMapper.deleteById(id);
        return Result.success();
    }

    //    查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        LambdaQueryWrapper<BookMark> wrapper = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(BookMark::getId, search);
        }
        Page<BookMark> userPage = bookMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(userPage);
    }
}
