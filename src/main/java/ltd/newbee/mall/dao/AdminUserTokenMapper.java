
package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.newbee.mall.entity.AdminUserToken;
import ltd.newbee.mall.entity.User;

public interface AdminUserTokenMapper extends BaseMapper<AdminUserToken> {

    int deleteByPrimaryKey(Long userId);

    int insert(AdminUserToken record);

    int insertSelective(AdminUserToken record);

    AdminUserToken selectByPrimaryKey(Long userId);

    AdminUserToken selectByToken(String token);

    int updateByPrimaryKeySelective(AdminUserToken record);

    int updateByPrimaryKey(AdminUserToken record);
}