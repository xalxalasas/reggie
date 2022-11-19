package com.itheima.reggie.service.impl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.commom.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.impl.CategoryService;
import com.itheima.reggie.service.impl.SetmealServicee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishServiceImpl dishService;
    @Autowired
    private SetmealServicee setmealServicee;

    /*根据id删除分类*/
    @Override
    public void remove(Long ids) {
        //先看看是否有关联菜品和套餐
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(dishLambdaQueryWrapper);
        if (count>0){
            throw new CustomException("当前分类关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealServicee.count(setmealLambdaQueryWrapper);
        if (count1>0){
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        super.removeById(ids);
    }
}
