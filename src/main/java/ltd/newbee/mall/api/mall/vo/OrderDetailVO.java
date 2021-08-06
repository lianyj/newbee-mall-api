
package ltd.newbee.mall.api.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单详情页页面VO
 */
@Data
public class OrderDetailVO implements Serializable {

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("订单日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date orderDate;

    @ApiModelProperty("快递费用")
    private Integer expressFee;

    @ApiModelProperty("备注")
    private String remark;



    @ApiModelProperty("客户id")
    private Long userId;

    @ApiModelProperty("客户名称")
    private String userName;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("联系地址")
    private String address;



    @ApiModelProperty("订单项列表")
    private List<OrderItemVO> OrderItemVOS;
}
