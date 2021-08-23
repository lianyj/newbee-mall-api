
package ltd.newbee.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItem  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long orderItemId;

    private Long orderId;

    private Long goodsId;

    private String goodsName;

    private Integer goodsCount;

    private Date createTime;

    @ApiModelProperty("商品规格")
    private String goodsIntro;

    @ApiModelProperty("商品原来价格")
    private BigDecimal originalPrice;

    @ApiModelProperty("实际价格")
    private BigDecimal sellingPrice;

}