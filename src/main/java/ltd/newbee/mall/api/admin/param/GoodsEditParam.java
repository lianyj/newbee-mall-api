
package ltd.newbee.mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GoodsEditParam {

    @ApiModelProperty("待修改商品id")
    @NotNull(message = "商品id不能为空")
    @Min(value = 1, message = "商品id不能为空")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    @NotEmpty(message = "商品名称不能为空")
    private String goodsName;

    @ApiModelProperty("商品简介")
    private String goodsIntro;

    @ApiModelProperty("originalPrice")
    @NotNull(message = "originalPrice不能为空")
    private BigDecimal originalPrice;

    @ApiModelProperty("库存")
    @NotNull(message = "库存不能为空")
    private Integer stockNum;

    @ApiModelProperty("商品标签")
    @NotEmpty(message = "商品标签不能为空")
    private String tag;

    private Byte goodsSellStatus;

    private String remark;
    private BigDecimal sphereMin;
    private BigDecimal sphereMax;
    private BigDecimal cylinderMin;
    private BigDecimal cylinderMax;
}