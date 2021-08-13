
package ltd.newbee.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.newbee.mall.api.mall.param.UserRegisterParam;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param loginName
     * @param password
     * @return
     */
    String register(UserRegisterParam UserRegisterParam);


    /**
     * 用户信息修改
     *
     * @param User
     * @return
     */
    Boolean updateUserInfo(UserRegisterParam User);



    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getUsersPage( Map<String,Object> params ,PageQueryUtil pageUtil);


    /**
     * 用户状态修改
     ** @return
     */
    Boolean updateUserStatus(Long userId,Integer status);

   List<User> getUserAllList();
}
