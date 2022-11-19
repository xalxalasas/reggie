package com.itheima.reggie.service.impl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.commom.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.impl.DishFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements com.itheima.reggie.service.impl.DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /*新增菜品及口味的保存*/
    @Transactional//多表操作，开启事务保证数据一致性
    @Override
    public void saveWithFlavor(DishDto dishDto){
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return  item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);


    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return  dishDto;
    }
@Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return  item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
@Transactional
    @Override
    public void deleteWithFlavor(@RequestParam List<Long> ids) {
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
    queryWrapper.in(Dish::getId,ids);
    queryWrapper.eq(Dish::getStatus,1);
    int count = this.count(queryWrapper);
    if (count>0){
        throw  new CustomException("菜品正在售卖，无法删除");
    }
    this.removeByIds(ids);
    LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    lambdaQueryWrapper.eq(DishFlavor::getDishId,ids);
    dishFlavorService.remove(lambdaQueryWrapper);


}
}
