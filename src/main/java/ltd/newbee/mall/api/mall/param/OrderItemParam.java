
package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单详情页页面订单项VO
 */
@Data
public class OrderItemParam implements Serializable {

    private Long orderId;

    private Long orderItemId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("规格型号")
    private String goodsIntro;

    @ApiModelProperty("商品数量")
    private Integer goodsCount;

}
