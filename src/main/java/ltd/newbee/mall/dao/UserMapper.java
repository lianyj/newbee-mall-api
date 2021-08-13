
package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {


    List<User> findUserList(@Param("params") Map params,@Param("pageUtil") PageQueryUtil pageUtil);

    int getTotalUsers(@Param("params")Map params,@Param("pageUtil")PageQueryUtil pageUtil);

}