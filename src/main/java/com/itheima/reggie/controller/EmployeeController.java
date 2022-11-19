package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.commom.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.impl.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping ("/employee")
@Slf4j
public class EmployeeController {
@Autowired
    private    EmployeeService employeeService;

/*员工登入*/
@PostMapping("/login")
public R<Employee> login( HttpServletRequest request,@RequestBody Employee employee){
    String password = employee.getPassword();
    password = DigestUtils.md5DigestAsHex(password.getBytes());
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Employee::getUsername,employee.getUsername());
    Employee emp = employeeService.getOne(queryWrapper);

    if (emp==null){
        return R.error("登入失败");
    }

    if (!emp.getPassword().equals(password)){
        return R.error("登入失败");
    }

    if (emp.getStatus()==0){
        return R.error("账号已禁用");
    }

    request.getSession().setAttribute("employee",emp.getId());
    return R.success(emp);


}
//员工退出
@PostMapping("logout")
public R<String> logout(HttpServletRequest request){
    //清理员工Session
    request.getSession().removeAttribute("employee");
    return R.success("退出成功");
}
//保存新增员工
@PostMapping
public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
    log.info("新增员工，员工信息{}",employee.toString());
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
/*    employee.setCreateTime(LocalDateTime.now());//获取现在时间
    employee.setUpdateTime(LocalDateTime.now());//更新时间
    Long empId  = (Long) request.getSession().getAttribute("employee");
    employee.setCreateUser(empId);
    employee.setUpdateUser(empId);*/
    employeeService.save(employee);
    return R.success("新增员工成功");
}
//员工分页查询
@GetMapping("/page")
public R<Page> page(int page,int pageSize,String name){
    log.info("page={},pageSize={},name={}",page,pageSize,name);
    //分页构造器
    Page<Employee> pageInfo = new Page(page, pageSize);
    //过滤
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
    if (name!=null){
        queryWrapper.like(Employee::getName,name);
    }
    queryWrapper.orderByDesc(Employee::getUpdateTime);
    employeeService.page(pageInfo,queryWrapper);
    return R.success(pageInfo);
}
/*修改员工*/
    @PutMapping
public R<String> updata(HttpServletRequest request,@RequestBody Employee employee){
        long id = Thread.currentThread().getId();
        log.info("线程id为{}",id);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
  log.info(employee.toString());
  return R.success("员工信息修改成功");
}
/*
* 工具id查询员工信息@PathVariable获取在路径的参数*/
@GetMapping("/{id}")
public R<Employee> getByIfId(@PathVariable Long id){
    Employee employee = employeeService.getById(id);
    if (employee!=null){
        return  R.success(employee);
    }
return R.error("没有查询到此员工信息");
}
}
