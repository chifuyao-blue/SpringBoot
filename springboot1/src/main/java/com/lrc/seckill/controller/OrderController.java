package com.lrc.seckill.controller;
import com.lrc.seckill.VO.OrderDetailVo;
import com.lrc.seckill.VO.RespBean;
import com.lrc.seckill.VO.RespBeanEnum;
import com.lrc.seckill.pojo.User;
import com.lrc.seckill.service.IOrderService;
import com.lrc.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService iOrderService;


    //订单详情
    @ModelAttribute("user")
    public User getUser(@CookieValue("userTicket") String ticket, HttpServletRequest request, HttpServletResponse response) {
        return userService.getUserByCookie(ticket, request, response);
    }
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(@ModelAttribute("user")User user,Long orderId){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo orderDetailVo = iOrderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }
}
