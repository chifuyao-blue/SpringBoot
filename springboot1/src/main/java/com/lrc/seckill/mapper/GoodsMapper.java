package com.lrc.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrc.seckill.VO.GoodsVO;
import com.lrc.seckill.pojo.Goods;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /***
     * 获取商品列表
     * @return
     */
    List<GoodsVO> findGoodsVO();

    /***
     * 获取商品详情
     * @return
     */
    GoodsVO findGoodsVOByGoodsId(Long goodsId);
}
