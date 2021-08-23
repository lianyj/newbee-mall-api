
package ltd.newbee.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminUserToken  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long adminUserId;

    private String token;

    private Date updateTime;

    private Date expireTime;
}