
package ltd.newbee.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminUserToken  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "admin_user_id", type = IdType.AUTO)
    private Long adminUserId;

    private String token;

    private Date updateTime;

    private Date expireTime;
}