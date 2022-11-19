package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.commom.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.impl.CategoryService;
import com.itheima.reggie.service.impl.DishFlavorService;
import com.itheima.reggie.service.impl.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/*菜品管理*/
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    //新增
@PostMapping
public R<String> save(@RequestBody DishDto dishDto){
    log.info(dishDto.toString());
    dishService.saveWithFlavor(dishDto);
return  R.success("新增菜品成功");
}
//查询
@GetMapping("/page")
public R<Page> page(int page ,int pageSize,String name){
    //创造分页构造器
    Page<Dish> pageinfo = new Page<>(page,pageSize);
    Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
    //创建查询构造器
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name!=null,Dish::getName,name);
    queryWrapper.orderByDesc(Dish::getUpdateTime);
    dishService.page(pageinfo,queryWrapper);
    BeanUtils.copyProperties(pageinfo,dishDtoPage,"records");
    List<Dish> records = pageinfo.getRecords();
    List<DishDto> list = records.stream().map((item)->{
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(item,dishDto);
        Long categoryId = item.getCategoryId();
        Category category = categoryService.getById(categoryId);
        String categoryName = category.getName();
        dishDto.setCategoryName(categoryName);
        return  dishDto;
    }).collect(Collectors.toList());
    dishDtoPage.setRecords(list);
    return R.success(dishDtoPage);
}
/*
* 根据id查菜品及口味*/
@GetMapping("/{id}")
public R<DishDto> get(@PathVariable Long id){
    DishDto dishDto = dishService.getByIdWithFlavor(id);
    return  R.success(dishDto);
}
//修改菜品
@PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return  R.success("修改菜品成功");
    }/*
    //根据菜类查菜
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    queryWrapper.eq(Dish::getStatus,1);
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = list.stream().map((item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        })).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
    //删除菜品
    @DeleteMapping
    public R<String> delete(Long[] ids) {
        List<Long> list = new ArrayList<>();
        for (Long id : ids) {
            list.add(id);
        }
        dishService.removeByIds(list);
        return R.success("删除成功");
    }
    //停起售菜品
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam Long[] ids) {

        List<Dish> dishes = new ArrayList<>();

        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishes.add(dish);
        }
        dishService.updateBatchById(dishes);
        return R.success("售卖状态修改成功!");
    }
}
