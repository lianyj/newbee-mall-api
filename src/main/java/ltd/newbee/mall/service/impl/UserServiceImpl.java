
package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.newbee.mall.api.mall.param.UserRegisterParam;
import ltd.newbee.mall.common.Exception;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.common.UserLevelCostEnum;
import ltd.newbee.mall.common.UserLevelEnum;
import ltd.newbee.mall.dao.UserMapper;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String register(UserRegisterParam UserRegisterParam) {
        User registerUser = new User();
        registerUser.setUserName(UserRegisterParam.getUserName());
        registerUser.setUserLevel(UserRegisterParam.getUserLevel());
        registerUser.setUserCost(UserLevelCostEnum.getUserLevelEnumByStatus(UserRegisterParam.getUserLevel()).getValue());
        registerUser.setContactName(UserRegisterParam.getContactName());
        registerUser.setMobile(UserRegisterParam.getMobile());
        registerUser.setAddress(UserRegisterParam.getAddress());
        registerUser.setRemark(UserRegisterParam.getRemark());
        if (userMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }


    @Override
    public Boolean updateUserInfo(UserRegisterParam User) {
        User user = userMapper.selectByPrimaryKey(User.getUserId());
        if (user == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        user.setUserName(User.getUserName());
        user.setUserLevel(User.getUserLevel());
        user.setUserCost(UserLevelCostEnum.getUserLevelEnumByStatus(User.getUserLevel()).getValue());
        user.setContactName(User.getContactName());
        user.setMobile(User.getMobile());
        user.setAddress(User.getAddress());
        user.setRemark(User.getRemark());
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public PageResult getUsersPage( Map<String,Object> params ,PageQueryUtil pageUtil) {
        List<User> Users = userMapper.findUserList(params,pageUtil);
        for (User user :Users){
            user.setUserLevelStr(UserLevelEnum.getUserLevelEnumByStatus(user.getUserLevel()).getName());
        }
        int total = userMapper.getTotalUsers(params,pageUtil);
        PageResult pageResult = new PageResult(Users, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean updateUserStatus(Long userId,Integer status){
        User user = new User();
        user.setUserId(userId);
        user.setIsDeleted(status.byteValue());
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }


}
