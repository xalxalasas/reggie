package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.commom.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.impl.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("测试");
      log.info("category:{}",category);
        categoryService.save(category);
        return  R.success("新增分类成功");
    }
    //分页方法
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize) {
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //排序构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        //开始查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    //删除分类
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除ccccc踩踩踩踩踩踩从{}",ids);
     /*   categoryService.removeById(ids);*/
        categoryService.remove(ids);
        return  R.success("分类删除成功");
    }
    //修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }
    /*查询分类*/
    @GetMapping("/list")
    public  R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();//条件构造器
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());//添加条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);//添加排序条件
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
