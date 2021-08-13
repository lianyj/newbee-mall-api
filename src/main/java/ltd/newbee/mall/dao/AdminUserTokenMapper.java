
package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.newbee.mall.entity.AdminUserToken;
import ltd.newbee.mall.entity.User;

public interface AdminUserTokenMapper extends BaseMapper<AdminUserToken> {


    AdminUserToken selectByToken(String token);

}