package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.commom.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.impl.CategoryService;
import com.itheima.reggie.service.impl.SetmealDishService;
import com.itheima.reggie.service.impl.SetmealServicee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/setmeal")
@RestController
public class SetmealController {
    @Autowired
    private SetmealServicee setmealServicee;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    //新增套餐
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString() + "新增套餐信息");
        setmealServicee.saveWithDish(setmealDto);
        return R.success("成功新增套餐");
    }

    //套餐分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={ }", page, pageSize, name);
        //分页构造器
        Page<SetmealDto> setmealDtoPage = new Page(page, pageSize);
        Page<Setmeal> pageInfo = new Page(page, pageSize);

        //过滤
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealServicee.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }
    //删除套餐
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("hhh哈哈哈哈"+ids.toString());
        setmealServicee.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }
    //停起售套餐
    @PostMapping("status/{status}")
    public R<String> status(@PathVariable int status, @RequestParam List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealServicee.getById(id);
            setmeal.setStatus(status);
            setmealServicee.updateById(setmeal);
        }
        return R.success("停起售成功");
    }
    //修改套餐
    @PutMapping
//    当修改数据或添加数据时清除缓存
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        log.info("setmealDto:{}", setmealDto);
//        修改套餐表
        setmealServicee.updateById(setmealDto);

////        修改套餐关联表
        Long id = setmealDto.getId();
        QueryWrapper<SetmealDish> wrapper = new QueryWrapper<>();
        wrapper.eq("setmeal_id", setmealDto.getId());
        setmealDishService.remove(wrapper);

//        保存提交过来的数据

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        List<SetmealDish> setmealDishList = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        setmealDishService.saveBatch(setmealDishes);

        return R.success("修改套餐成功");
    }
    //回显套餐
    @GetMapping("/{id}")
    public R<SetmealDto> update(@PathVariable Long id) {

//        查询套餐表
        Setmeal setmeal = setmealServicee.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);

//        查询套餐关联表
        QueryWrapper<SetmealDish> wrapper = new QueryWrapper<>();
        wrapper.eq("setmeal_id", id);
        List<SetmealDish> dishList = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(dishList);
        return R.success(setmealDto);
    }
    //根据条件查套餐
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper  = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getStatus,setmeal.getStatus()).orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealServicee.list(queryWrapper);
        return  R.success(list);
    }
}
