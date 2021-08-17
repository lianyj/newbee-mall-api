
package ltd.newbee.mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GoodsAddParam {

    @ApiModelProperty("商品名称")
    @NotEmpty(message = "商品名称不能为空")
    private String goodsName;

    @ApiModelProperty("商品简介")
    private String goodsIntro;

    @ApiModelProperty("标批价格")
    @NotNull(message = "标批价格不能为空")
    private BigDecimal originalPrice;

    @ApiModelProperty("库存")
    private Integer stockNum;

    @ApiModelProperty("商品标签")
    private String tag;

    private Byte goodsSellStatus;

    @ApiModelProperty("备注")
    private String remark;

    private BigDecimal sphereMin;
    private BigDecimal sphereMax;
    private BigDecimal cylinderMin;
    private BigDecimal cylinderMax;
}