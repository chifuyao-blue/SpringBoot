package com.lrc.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lrc.seckill.RabbitMQ.MQSender;
import com.lrc.seckill.VO.GoodsVO;
import com.lrc.seckill.VO.RespBean;
import com.lrc.seckill.VO.RespBeanEnum;

import com.lrc.seckill.config.AccessLimit;
import com.lrc.seckill.exception.GlobalException;
import com.lrc.seckill.pojo.*;

import com.lrc.seckill.service.*;
import com.lrc.seckill.utils.JsonUtil;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Controller
@RequestMapping("/secKill")
@Slf4j
public class SecKillController implements InitializingBean{

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private RedisScript<Long> script;

    private Map<Long,Boolean>EmptyStockMap = new HashMap<>();


    /***
     * 秒杀
     */
    @RequestMapping("/doSecKill2")
    public String doSecKill2(Model model, User user, Long goodsId) {
        //判断用户
        if (user == null) {
            return "login";
        }

        model.addAttribute("user", user);
        //判断库存
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK_ERROR.getMessage());
            return "secKillFail";
        }
        //判断订单(是否重复抢购)
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "secKillFail";
        }
        //抢购
        Order order = orderService.secKill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }


//静态化改版
@ModelAttribute("user")
public User getUser(@CookieValue("userTicket") String ticket, HttpServletRequest request, HttpServletResponse response) {
    return userService.getUserByCookie(ticket, request, response);
}
    @RequestMapping(value = "/{path}/doSecKill1",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill1(@PathVariable String path , @ModelAttribute("user")User user, Long goodsId) {
        //判断用户
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations   valueOperations = redisTemplate.opsForValue();
        //接口判断
        boolean check = orderService.checkPath(user,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //通过redis获取判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
        //已静态化，不需要model
        //model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
        /*return "secKillFail";*/
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        //内存标记，减少redis访问
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
        }
        //预减库存操作
        //Long stock  = valueOperations.decrement("seckillGoods:"+goodsId);
        //脚本判断库存
         Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId),
                 Collections.EMPTY_LIST);
        if(stock < 0){
            EmptyStockMap.put(goodsId,true);
            //valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
        }

        /*Order order = orderService.secKill(user, goods);*/
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);




        /*//判断库存
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK_ERROR.getMessage());
            *//*return "secKillFail";*//*
            return RespBean.error(RespBeanEnum.EMPTY_STOCK_ERROR);
        }*/
        /*//判断订单(是否重复抢购)
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
*/
        //通过redis获取判断是否重复抢购
       /* SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {*/
            //已静态化，不需要model
            //model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            /*return "secKillFail";*/
          /*  return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }*/
        //抢购
       /* Order order = orderService.secKill(user, goods);*/
       /* model.addAttribute("order", order);
        model.addAttribute("goods", goods);*/
        /*return "orderDetail";*/
        /*return RespBean.success(order);*/

    }

    //获取秒杀结果
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(@ModelAttribute("user")User user,Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    //获取秒杀地址
    @AccessLimit(second = 5,maxCount = 5,needLogin= true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(@ModelAttribute("user")User user,Long goodsId,String captcha,HttpServletRequest request){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        /*ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问5秒5次
        String uri = request.getRequestURI();
        captcha = "0";
        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        if(count == null){
            valueOperations.set(uri+":"+user.getId(),1,5,TimeUnit.SECONDS);
        }else if(count<5){
            valueOperations.increment(uri+":"+user.getId());
        }else {
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }*/
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    //验证码
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(@ModelAttribute("user")User user, Long goodsId, HttpServletResponse response) {
        if (null==user||goodsId<0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出图片类型
       /* response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32,3);*/
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 5);
        String verCode = captcha.text().toLowerCase();
        System.out.println(user.getId());
        System.out.println(goodsId);
        System.out.println(captcha.text());
        System.out.println(captcha);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, verCode,300, TimeUnit.SECONDS);
        System.out.println(user.getId());
        System.out.println("qw");
        try {
            System.out.println("qw");
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }


    //系统初始化方法,把商品库存加到redis中
    @Override
    public void afterPropertiesSet() throws Exception {

        List<GoodsVO>list = goodsService.findGoodsVO();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVO ->{
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVO.getId(),goodsVO.getStockCount());
            EmptyStockMap.put(goodsVO.getId(),false);
        }
        );
    }
}
