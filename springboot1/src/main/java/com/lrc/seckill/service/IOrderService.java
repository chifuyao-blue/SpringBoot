package com.lrc.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrc.seckill.VO.GoodsVO;
import com.lrc.seckill.VO.OrderDetailVo;
import com.lrc.seckill.pojo.Order;
import com.lrc.seckill.pojo.User;


public interface IOrderService extends IService<Order> {

    /***
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order secKill(User user, GoodsVO goods);


    /***
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);



    /***
     * 创建秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);



    /***
     * 秒杀路径校验
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(User user, Long goodsId, String path);



    /***
     * 验证码校验
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
