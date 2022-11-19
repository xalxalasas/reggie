package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SetmealServicee extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);//保存新增套餐
    public  void  removeWithDish(@RequestParam List<Long> ids);//删除套餐
}
