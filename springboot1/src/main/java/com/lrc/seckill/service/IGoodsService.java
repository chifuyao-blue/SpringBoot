package com.lrc.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrc.seckill.VO.GoodsVO;
import com.lrc.seckill.pojo.Goods;

import java.util.List;


public interface IGoodsService extends IService<Goods> {
    /***
     * 返回商品列表
     * @return
     */
    List<GoodsVO> findGoodsVO();

    /***
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVO findGoodsVOByGoodsId(Long goodsId);
}
