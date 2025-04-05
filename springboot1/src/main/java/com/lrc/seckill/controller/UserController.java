package com.lrc.seckill.controller;


import com.lrc.seckill.RabbitMQ.MQSender;
import com.lrc.seckill.VO.RespBean;

import com.lrc.seckill.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;


   /* //用户信息测试
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

    //rabbitmq测试发送消息
    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }

    //fanout模式
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq01(){
        mqSender.send("hello!");
    }

    //direct模式
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq02(){
        mqSender.send01("hello,red");
    }

    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq03(){
        mqSender.send02("hello,green");
    }

    //topic模式
    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void mq04(){
        mqSender.send03("hello,QUEUE01");
    }
    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void mq05(){
        mqSender.send04("hello,QUEUE们");
    }

    //headers模式
    @RequestMapping("/mq/headers01")
    @ResponseBody
    public void mq06(){
        mqSender.send05("hello,queue01");
    }
    @RequestMapping("/mq/headers02")
    @ResponseBody
    public void mq07(){
        mqSender.send06("hello,queue02");
    }*/
}
