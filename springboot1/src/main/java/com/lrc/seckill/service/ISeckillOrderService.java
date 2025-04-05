package com.lrc.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrc.seckill.pojo.SeckillOrder;
import com.lrc.seckill.pojo.User;


public interface ISeckillOrderService extends IService<SeckillOrder> {

    /***
     * 获取秒杀结果
     * @param user
     * @param goodsId orderId成功 -1失败 0排队中
     * @return
     */
    Long getResult(User user, Long goodsId);


}
