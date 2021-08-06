
package ltd.newbee.mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ltd.newbee.mall.api.admin.param.BatchIdParam;
import ltd.newbee.mall.api.mall.param.OrderDetailParam;
import ltd.newbee.mall.api.mall.vo.OrderDetailVO;
import ltd.newbee.mall.common.Exception;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.annotation.TokenToAdminUser;
import ltd.newbee.mall.config.annotation.MallTokenToUser;
import ltd.newbee.mall.entity.AdminUserToken;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@RestController
@Api(value = "v1", tags = "8-5.后台管理系统订单模块接口")
@RequestMapping("/manage-api/v1")
public class AdminOrderAPI {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderAPI.class);

    @Autowired
    private OrderService orderService;


    /**
     * 列表
     */
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    @ApiOperation(value = "订单列表", notes = "可根据订单号和订单状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "订单时间") String[] orderTime,
                       @RequestParam(required = false) @ApiParam(value = "客户ID") Integer userId,
                       @RequestParam(required = false) @ApiParam(value = "快递状态") Integer expressStatus,
                       @RequestParam(required = false) @ApiParam(value = "订单号") String orderNo,
                       @RequestParam(required = false) @ApiParam(value = "订单状态") Integer orderStatus,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (!StringUtils.isEmpty(orderNo)) {
            params.put("orderNo", orderNo);
        }
        if (orderStatus != null) {
            params.put("orderStatus", orderStatus);
        }
        if (userId != null) {
            params.put("userId", userId);
        }
        if (expressStatus != null) {
            params.put("expressStatus", expressStatus);
        }
        if (orderTime != null && orderTime.length>0) {
            params.put("startTime", orderTime[0]);
            params.put("endTime", orderTime[1]);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(orderService.getOrdersPage(pageUtil));
    }

    @GetMapping("/orders/{orderId}")
    @ApiOperation(value = "订单详情接口", notes = "传参为订单号")
    public Result<OrderDetailVO> orderDetailPage(@ApiParam(value = "订单号") @PathVariable("orderId") Long orderId, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        return ResultGenerator.genSuccessResult(orderService.getOrderDetailByOrderId(orderId));
    }

    /**
     * 关闭订单
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.PUT)
    @ApiOperation(value = "修改订单状态为商家关闭", notes = "批量修改")
    public Result closeOrder(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = orderService.closeOrder(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    @PostMapping("/saveOrder")
    @ApiOperation(value = "生成订单接口", notes = "")
    public Result<String> saveOrder(@ApiParam(value = "订单参数") @RequestBody OrderDetailParam saveOrderParam, @MallTokenToUser  AdminUserToken adminUser) {
        if (saveOrderParam == null || saveOrderParam.getOrderItemVOS() == null || saveOrderParam.getUserId() == null) {
             Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
         }
         if (saveOrderParam.getOrderItemVOS().size() < 1) {
             Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
          }
        String result = orderService.saveOrder(adminUser.getAdminUserId(), saveOrderParam);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @PostMapping("/updateOrder")
    @ApiOperation(value = "修改订单", notes = "")
    public Result<String> updateOrderInfo(@ApiParam(value = "订单参数") @RequestBody OrderDetailParam saveOrderParam, @MallTokenToUser  AdminUserToken adminUser) {
        if (saveOrderParam == null || saveOrderParam.getOrderItemVOS() == null|| saveOrderParam.getOrderId() == null || saveOrderParam.getUserId() == null) {
            Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if (saveOrderParam.getOrderItemVOS().size() < 1) {
            Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        String result = orderService.updateOrderInfo(adminUser.getAdminUserId(), saveOrderParam);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
}