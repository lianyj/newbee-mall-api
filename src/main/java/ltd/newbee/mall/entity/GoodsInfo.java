
package ltd.newbee.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsInfo  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "goods_id", type = IdType.AUTO)
    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private BigDecimal originalPrice;

    private Integer stockNum;

    private String tag;

    private Byte goodsSellStatus;

    private Long createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String remark;
    private BigDecimal sphereMin;
    private BigDecimal sphereMax;
    private BigDecimal cylinderMin;
    private BigDecimal cylinderMax;
}