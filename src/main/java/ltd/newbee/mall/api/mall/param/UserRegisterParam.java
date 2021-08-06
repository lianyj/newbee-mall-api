
package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ltd.newbee.mall.common.UserLevelCostEnum;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 用户注册param
 */
@Data
public class UserRegisterParam implements Serializable {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("客户名称")
    @NotEmpty(message = "客户名称不能为空")
    private String userName;

    @ApiModelProperty("用户级别")
    private Integer userLevel;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("联系地址")
    private String address;

    @ApiModelProperty("备注")
    private String remark;
}
