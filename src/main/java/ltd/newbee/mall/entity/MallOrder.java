
package ltd.newbee.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MallOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId( type = IdType.AUTO)
    private Long orderId;

    private String orderNo;

    private Long userId;

    private BigDecimal totalPrice;

    private Integer orderStatus;

    private Integer isDeleted;

    private Integer expressFee;

    private Integer expressStatus;

    private String remark;

    private Long opUserId;

    private String payType;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    //客户名称
    @TableField(exist = false)
    private String userName;
    //联系人
    @TableField(exist = false)
    private String contactName;
    @TableField(exist = false)
    private String userLevelStr;
    @TableField(exist = false)
    private String expressStatusStr;
    @TableField(exist = false)
    private String orderStatusStr;
}