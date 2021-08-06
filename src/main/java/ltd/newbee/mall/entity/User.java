
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
public class User  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    //客户名称
    private String userName;
    //联系人
    private String contactName;
    //客户等级
    private Integer userLevel;
    //客户等级
    @TableField(exist = false)
    private String userLevelStr;
    //客户折扣
    private Double userCost;
    //联系电话
    private String mobile;
    //联系地址
    private String address;
    //备注
    private String remark;
    //0-正常 1-已注销
    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


}