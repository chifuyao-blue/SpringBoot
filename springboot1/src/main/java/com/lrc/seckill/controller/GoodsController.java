package com.lrc.seckill.controller;


import com.lrc.seckill.VO.DetailVo;
import com.lrc.seckill.VO.GoodsVO;
import com.lrc.seckill.VO.RespBean;
import com.lrc.seckill.pojo.User;
import com.lrc.seckill.service.IGoodsService;
import com.lrc.seckill.service.IUserService;
/*import com.sun.deploy.net.HttpResponse;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.HttpResource;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//商品
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

/*  @RequestMapping("/toList")
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("userTicket") String ticket) {
        if(StringUtils.isEmpty(ticket)) {
            return "login";
        }
        //User user = (User) session.getAttribute(ticket);
        User user = userService.getUserByCookie(ticket, request, response);
        if(null == user) {
            return "login";
        }
        model.addAttribute("user", user);
        return "goodsList";
    }*/

    /***
     * 获取商品列表并跳转到商品列表页面
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        //redis中获取页面信息，如果不为空则直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVO());
        //如果为空，则手动渲染，并存入redis
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        //return "goodsList";//跳到goodsList
        return html;
    }

    /***
     * 获取商品信息并跳转到商品详情页面
     *
     * **/
    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model,  @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
        //读取redis缓存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail" + goodsId);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        // 从Cookie中获取userTicket
        Cookie[] cookies = request.getCookies();
        String userTicket = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userTicket".equals(cookie.getName())) {
                    userTicket = cookie.getValue();
                    break;
                }
            }
        }

        // 根据userTicket获取用户信息
        User user = userService.getUserByCookie(userTicket, request, response);
        model.addAttribute("user", user);
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        Date startDate = goodsVO.getStartDate();
        Date endDate = goodsVO.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus;
        //倒计时
        int remainSeconds;
        //秒杀没开始
        if (nowDate.before(startDate)) {
            secKillStatus = 0;
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            //秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            //秒杀进行中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("goods", goodsVO);
        //手动缓存redis
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail"+goodsId,html,60,TimeUnit.SECONDS);
        }
        return html;
        //return "goodsDetail";
    }

    @ModelAttribute("user")
    public User getUser(@CookieValue("userTicket") String ticket, HttpServletRequest request, HttpServletResponse response) {
        return userService.getUserByCookie(ticket, request, response);
    }

    /***
     * 获取商品信息并跳转到商品详情页面

     */
   /* public User user(@CookieValue("userTicket")String ticket,HttpServletRequest request, HttpServletResponse response){
        User user = userService.getUserByCookie( ticket,request,response);
        return user;
    }*/
    @RequestMapping(value = "/toDetail1/{goodsId}")
    @ResponseBody
    public RespBean toDetail1(@ModelAttribute("user")User user, @PathVariable Long goodsId,HttpServletRequest request, HttpServletResponse response) {
        // 根据userTicket获取用户信息
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        Date startDate = goodsVO.getStartDate();
        Date endDate = goodsVO.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus;
        //倒计时
        int remainSeconds;
        //秒杀没开始
        if (nowDate.before(startDate)) {
            secKillStatus = 0;
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            //秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            //秒杀进行中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVO(goodsVO);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);


        //return "goodsDetail";
        return RespBean.success(detailVo);
    }
}
