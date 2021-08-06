
package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ltd.newbee.mall.api.mall.vo.OrderItemVO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单详情页页面VO
 */
@Data
public class OrderDetailParam implements Serializable {

    private Long orderId;

    @ApiModelProperty("客户id")
    private Long userId;

    private Integer orderStatus;

    private Integer expressStatus;

    @ApiModelProperty("快递费用")
    private Integer expressFee;

    @ApiModelProperty("备注")
    private String remark;

    private Date orderDate;

    private String payType;


    @ApiModelProperty("订单项列表")
    private List<OrderItemVO> OrderItemVOS;
}
