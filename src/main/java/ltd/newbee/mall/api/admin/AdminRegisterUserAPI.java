
package ltd.newbee.mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ltd.newbee.mall.api.mall.param.UserRegisterParam;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.annotation.TokenToAdminUser;
import ltd.newbee.mall.entity.AdminUserToken;
import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@RestController
@Api(value = "v1", tags = "8-6.后台管理系统注册用户模块接口")
@RequestMapping("/manage-api/v1")
public class AdminRegisterUserAPI {

    private static final Logger logger = LoggerFactory.getLogger(AdminRegisterUserAPI.class);

    @Autowired
    private UserService userService;

    /**
     * 列表
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ApiOperation(value = "商城注册用户列表", notes = "商城注册用户列表")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "用户ID") Integer userId,
                       @RequestParam(required = false) @ApiParam(value = "用户等级") Integer userLevel,
                       @RequestParam(required = false) @ApiParam(value = "用户名称") String userName,
                       @RequestParam(required = false) @ApiParam(value = "联系人") String contactName,
                       @RequestParam(required = false) @ApiParam(value = "联系电话") String mobile,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Map<String,Object> params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("userId", userId);
        params.put("userLevel", userLevel);
        params.put("userName", userName);
        params.put("contactName", contactName);
        params.put("mobile", mobile);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(userService.getUsersPage(params,pageUtil));
    }



    @PostMapping("/user/add")
    @ApiOperation(value = "新增用户", notes = "")
    public Result add(@RequestBody  UserRegisterParam UserRegisterParam) {
        String registerResult = userService.register(UserRegisterParam);
        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @PostMapping("/user/update")
    @ApiOperation(value = "修改用户信息", notes = "")
    public Result updateInfo(@RequestBody UserRegisterParam UserUpdateParam) {
        Boolean flag = userService.updateUserInfo(UserUpdateParam);
        if (flag) {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            //返回失败
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    @RequestMapping(value = "/users/status", method = RequestMethod.GET)
    @ApiOperation(value = "修改用户状态", notes = "")
    public Result updateInfo(@RequestParam(required = true)  Long userId,
                             @RequestParam(required = true) Integer status) {
        Boolean flag = userService.updateUserStatus( userId, status);
        if (flag) {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            //返回失败
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

}