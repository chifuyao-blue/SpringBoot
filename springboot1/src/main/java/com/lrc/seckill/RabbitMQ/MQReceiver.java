package com.lrc.seckill.RabbitMQ;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lrc.seckill.VO.GoodsVO;
import com.lrc.seckill.VO.RespBean;
import com.lrc.seckill.VO.RespBeanEnum;
import com.lrc.seckill.pojo.SeckillMessage;
import com.lrc.seckill.pojo.SeckillOrder;
import com.lrc.seckill.pojo.User;
import com.lrc.seckill.service.IGoodsService;
import com.lrc.seckill.service.IOrderService;
import com.lrc.seckill.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 类的简要描述
 *
 * @author 33182
 * @Description: 消息消费者
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IOrderService orderService;

    /*@RabbitListener(queues = "queue")
    public void receive(Object msg){
        log.info("接收信息："+msg);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receive01(Object msg){
        log.info("QUEUE01接收消息："+msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receive02(Object msg){
        log.info("QUEUE02接收消息："+msg);
    }

    @RabbitListener(queues = "queue_direct01")
    public void receive03(Object msg){
        log.info("QUEUE01接收消息："+msg);
    }

    @RabbitListener(queues = "queue_direct02")
    public void receive04(Object msg){
        log.info("QUEUE02接收消息"+msg);
    }

    //topic模式
    @RabbitListener(queues = "queue_topic01")
    public void receive05(Object msg){
        log.info("QUEUE01接收消息"+msg);
    }

    @RabbitListener(queues = "queue_topic02")
    public void receive06(Object msg){
        log.info("QUEUE02接收消息"+msg);
    }

    //headers模式
    @RabbitListener(queues = "queue_headers01")
    public void receive07(Message message){
        log.info("QUEUE01接收message对象"+message);
        log.info("QUEUE01接受消息"+new String(message.getBody()));
    }
    @RabbitListener(queues = "queue_headers02")
    public void receive08(Message message){
        log.info("QUEUE02接收message对象"+message);
        log.info("QUEUE02接受消息"+new String(message.getBody()));
    }*/

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收消息："+ message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message,SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        //判断库存
        if(goodsVO.getStockCount()<1){
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            //已静态化，不需要model
            //model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            /*return "secKillFail";*/
            return ;
        }
        orderService.secKill(user,goodsVO);
    }
}
