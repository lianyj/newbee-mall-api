
package ltd.newbee.mall.api.mall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单详情页页面订单项VO
 */
@Data
public class OrderItemVO implements Serializable {

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("规格型号")
    private String goodsIntro;

    @ApiModelProperty("商品数量")
    private Integer goodsCount;

    @ApiModelProperty("产品原来价格")
    private BigDecimal originalPrice;

    @ApiModelProperty("商品价格")
    private BigDecimal sellingPrice;
}
