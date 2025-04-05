package com.lrc.seckill.VO;

import com.lrc.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类的简要描述
 *
 * @author 33182
 * @Description: 订单详情对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private Order order;
    private GoodsVO goodsVO;

}
