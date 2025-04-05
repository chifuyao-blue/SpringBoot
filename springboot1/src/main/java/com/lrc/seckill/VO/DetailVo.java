package com.lrc.seckill.VO;

import com.lrc.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类的简要描述
 *
 * @author 33182
 * @Description: 详情返回对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class DetailVo {
    private User user;
    private GoodsVO goodsVO;
    private int secKillStatus;
    private int remainSeconds;

}
