package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.commom.BaseContext;
import com.itheima.reggie.commom.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
* 检擦过滤器
* urlPattens过滤路径*/
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;

        //获取请求uri
        String requestURI = request.getRequestURI();
        log.info("拦截到：{}",requestURI);
        //是否要处理
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次请求{}不需处理",requestURI);
            filterChain.doFilter(request,response);//放行
            return;
        }

        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户{}已经登入本次请求不需处理",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");

            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);//放行
            return;
        }
        //前端
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户{}已经登入本次请求不需处理",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");

            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);//放行
            return;
        }

        //通过输出流向客户端相应数据
        log.info("用户未登入");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));return;

    }

    //路径匹配方法
    public boolean check(String[] urls,String requestURL){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match){
                return true;
            }
        }
        return false;
    }
}
