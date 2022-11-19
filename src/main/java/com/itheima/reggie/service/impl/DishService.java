package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);//新增菜品
    public DishDto getByIdWithFlavor(Long id);//根据id查DishDto（两张表）

    public void updateWithFlavor(DishDto dishDto);//跟新菜品及口味

    public  void deleteWithFlavor(@RequestParam List<Long> ids);
}
