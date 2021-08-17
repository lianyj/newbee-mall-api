
package ltd.newbee.mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ltd.newbee.mall.api.admin.param.BatchIdParam;
import ltd.newbee.mall.api.mall.param.OrderDetailParam;
import ltd.newbee.mall.api.mall.param.OrderItemParam;
import ltd.newbee.mall.api.mall.vo.OrderDetailVO;
import ltd.newbee.mall.api.mall.vo.OrderItemVO;
import ltd.newbee.mall.common.Exception;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.annotation.TokenToAdminUser;
import ltd.newbee.mall.entity.AdminUserToken;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.DateUtils;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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
                       @RequestParam(required = false) @ApiParam(value = "订单时间") String startTime,
                       @RequestParam(required = false) @ApiParam(value = "订单时间") String endTime,
                       @RequestParam(required = false) @ApiParam(value = "客户ID") Integer userId,
                       @RequestParam(required = false) @ApiParam(value = "快递状态") Integer expressStatus,
                       @RequestParam(required = false) @ApiParam(value = "订单号") String orderNo,
                       @RequestParam(required = false) @ApiParam(value = "订单状态") Integer orderStatus,
                       @TokenToAdminUser AdminUserToken adminUser) {
        try {
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
            if (startTime != null ) {
                params.put("startTime", startTime);
            }
            if (endTime != null ) {
                params.put("endTime",endTime);
            }
            PageQueryUtil pageUtil = new PageQueryUtil(params);
            return ResultGenerator.genSuccessResult(orderService.getOrdersPage(pageUtil));
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }

    /**
     * 订单列表导出
     */
    @RequestMapping(value = "/orders/export", method = RequestMethod.GET)
    @ApiOperation(value = "订单列表导出", notes = "")
    public void export(
            @RequestParam(required = false) @ApiParam(value = "订单时间") String startTime,
                       @RequestParam(required = false) @ApiParam(value = "订单时间") String endTime,
                       @RequestParam(required = false) @ApiParam(value = "客户ID") Integer userId,
                       @RequestParam(required = false) @ApiParam(value = "快递状态") Integer expressStatus,
                       @RequestParam(required = false) @ApiParam(value = "订单号") String orderNo,
                       @RequestParam(required = false) @ApiParam(value = "订单状态") Integer orderStatus,
                        HttpServletResponse response) {
        try {

            Map<String,Object> params = new HashMap<String,Object>();
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
            if (startTime != null ) {
                params.put("startTime", startTime);
            }
            if (endTime != null ) {
                params.put("endTime",endTime);
            }
            PageQueryUtil pageUtil = new PageQueryUtil(params);
            orderService.exportOrdersList(pageUtil,response);
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
    }

    @GetMapping("/orders/{orderId}")
    @ApiOperation(value = "订单详情接口", notes = "传参为订单号")
    public Result<OrderDetailVO> orderDetailPage(@ApiParam(value = "订单号") @PathVariable("orderId") Long orderId,
                                                 @TokenToAdminUser AdminUserToken adminUser) {
        try {
            logger.info("adminUser:{}", adminUser.toString());
            return ResultGenerator.genSuccessResult(orderService.getOrderDetailByOrderId(orderId,adminUser.getAdminUserId()));
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }

    /**
     * 关闭订单
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.GET)
    @ApiOperation(value = "修改订单状态为商家关闭", notes = "批量修改")
    public Result closeOrder(@RequestParam("orderId") Long orderId,
                             @TokenToAdminUser AdminUserToken adminUser) {
        try {
            logger.info("adminUser:{}", adminUser.toString());
            if (orderId==null) {
                return ResultGenerator.genFailResult("参数异常！");
            }
            String result = orderService.closeOrder(orderId);
            if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult(result);
            }
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }


    @PostMapping("/order/save")
    @ApiOperation(value = "生成订单接口", notes = "")
    public Result<String> saveOrder(@ApiParam(value = "订单参数") @RequestBody OrderDetailParam saveOrderParam,
                                    @TokenToAdminUser  AdminUserToken adminUser) {
        try {
            logger.info("adminUser:{}", adminUser.toString());
            if (saveOrderParam == null || saveOrderParam.getUserId() == null) {
                Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
            }
            String result = orderService.saveOrder(adminUser.getAdminUserId(), saveOrderParam);
            if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult(result);
            }
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }

    @PostMapping("/order/edit")
    @ApiOperation(value = "修改订单信息接口", notes = "")
    public Result<String> editOrderInfo(@ApiParam(value = "订单参数") @RequestBody OrderDetailParam saveOrderParam,
                                        @TokenToAdminUser  AdminUserToken adminUser) {
        try {
            logger.info("adminUser:{}", adminUser.toString());
            if (saveOrderParam == null || saveOrderParam.getUserId() == null || saveOrderParam.getOrderId() == null) {
                Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
            }
            String result = orderService.editOrderInfo(adminUser.getAdminUserId(), saveOrderParam);
            if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult(result);
            }
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }

    @PostMapping("/updateOrder")
    @ApiOperation(value = "修改订单", notes = "")
    public Result<String> updateOrderInfo(@ApiParam(value = "订单参数") @RequestBody OrderDetailParam saveOrderParam,
                                          @TokenToAdminUser  AdminUserToken adminUser) {
        try {
            logger.info("adminUser:{}", adminUser.toString());
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
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }

    @GetMapping("/order/item/delete/{itemId}")
    @ApiOperation(value = "删除订单产品", notes = "传参为订单号")
    public Result<OrderDetailVO> deleteItem(@ApiParam(value = "订单号") @PathVariable("itemId") Long itemId) {
        try {
            return ResultGenerator.genSuccessResult(orderService.deleteItem(itemId));
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }

    @GetMapping("/orders/status/change")
    @ApiOperation(value = "修改订单状态", notes = "")
    public Result<OrderDetailVO> changeOrderStatus( @RequestParam("orderId") Long orderId, @RequestParam("orderStatus") Integer orderStatus) {
        try {
            String result = orderService.changeOrderStatus(orderId,orderStatus);
            if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult(result);
            }
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }

    @GetMapping("/orders/express/change")
    @ApiOperation(value = "修改快递状态", notes = "传参为订单号")
    public Result<OrderDetailVO> changeExpressStatus( @RequestParam("orderId") Long orderId, @RequestParam("expressStatus") Integer expressStatus) {
        try {
            String result = orderService.changeExpressStatus(orderId,expressStatus);
            if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult(result);
            }
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }


    @PostMapping("/order/item/update")
    @ApiOperation(value = "修改订单产品", notes = "")
    public Result<String> updateItem(@ApiParam(value = "订单参数") @RequestBody OrderItemParam orderItemParam,
                                     @TokenToAdminUser  AdminUserToken adminUser) {
        try {
            logger.info("adminUser:{}", adminUser.toString());
            if (orderItemParam == null || orderItemParam.getOrderItemId() == null|| orderItemParam.getGoodsId() == null|| orderItemParam.getOrderId() == null) {
                Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
            }
            String result = orderService.updateItem(adminUser.getAdminUserId(), orderItemParam);
            if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult(result);
            }
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }

    @PostMapping("/order/item/add")
    @ApiOperation(value = "新增订单产品", notes = "")
    public Result<String> addItem(@ApiParam(value = "订单参数") @RequestBody OrderItemParam orderItemParam,
                                  @TokenToAdminUser  AdminUserToken adminUser) {
        try {
            logger.info("adminUser:{}", adminUser.toString());
            if (orderItemParam == null || orderItemParam.getGoodsId() == null|| orderItemParam.getOrderId() == null) {
                Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
            }
            String result = orderService.addItem(adminUser.getAdminUserId(), orderItemParam);
            if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult(result);
            }
        }catch (java.lang.Exception e){
            logger.error("出异常：",e);
        }
        return ResultGenerator.genFailResult("请稍后再试");
    }
}