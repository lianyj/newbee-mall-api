
package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.newbee.mall.api.mall.param.OrderDetailParam;
import ltd.newbee.mall.api.mall.vo.OrderDetailVO;
import ltd.newbee.mall.api.mall.vo.OrderItemVO;
import ltd.newbee.mall.common.*;
import ltd.newbee.mall.common.Exception;
import ltd.newbee.mall.dao.GoodsInfoMapper;
import ltd.newbee.mall.dao.OrderItemMapper;
import ltd.newbee.mall.dao.MallOrderMapper;
import ltd.newbee.mall.dao.UserMapper;
import ltd.newbee.mall.entity.MallOrder;
import ltd.newbee.mall.entity.OrderItem;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.DateUtils;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder> implements OrderService {

    @Autowired
    private MallOrderMapper mallOrderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public OrderDetailVO getOrderDetailByOrderId(Long orderId) {
        MallOrder Order = mallOrderMapper.selectByPrimaryKey(orderId);
        if (Order == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        User User = userMapper.selectById(Order.getUserId());
        if (User == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(Order.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(orderItems)) {
            OrderDetailVO OrderDetailVO = new OrderDetailVO();
            //订单信息
            OrderDetailVO.setOrderNo(Order.getOrderNo());
            OrderDetailVO.setOrderDate(Order.getOrderDate());
            OrderDetailVO.setExpressFee(Order.getExpressFee());
            OrderDetailVO.setRemark(Order.getRemark());
            //客户信息
            OrderDetailVO.setUserName(User.getUserName());
            OrderDetailVO.setContactName(User.getContactName());
            OrderDetailVO.setMobile(User.getMobile());
            OrderDetailVO.setAddress(User.getAddress());
            //商品信息
            List<OrderItemVO> OrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
            OrderDetailVO.setOrderItemVOS(OrderItemVOS);
            return OrderDetailVO;
        } else {
            Exception.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
            return null;
        }
    }




    @Override
    public String saveOrder(Long adminUserId, OrderDetailParam saveOrderParam) {

        //总价
        BigDecimal priceTotal = BigDecimal.ZERO;
        for (OrderItemVO OrderItemVO : saveOrderParam.getOrderItemVOS()) {
            priceTotal = priceTotal.add( OrderItemVO.getSellingPrice().multiply(BigDecimal.valueOf(OrderItemVO.getGoodsCount())) );
        }

        User User = userMapper.selectById(saveOrderParam.getUserId());
        if (User == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }

        //1.创建订单
        MallOrder Order = new MallOrder();
        //用户等级 + 付款方式 + yyymmdd+数量
        String userLevel = UserLevelEnum.getUserLevelEnumByStatus(User.getUserLevel()).getName();
        Integer count = countTodayOrder(saveOrderParam.getOrderDate());
        String number = String.format("%04d", count);
        String orderNo = userLevel + saveOrderParam.getPayType() + DateUtils.format(saveOrderParam.getOrderDate() ,"yyyyMMdd") + number;
        Order.setOrderNo(orderNo);
        Order.setUserId(saveOrderParam.getUserId());
        Order.setOrderStatus(saveOrderParam.getOrderStatus());
        Order.setExpressStatus(saveOrderParam.getExpressStatus());
        Order.setRemark(saveOrderParam.getRemark());
        Order.setOrderDate(saveOrderParam.getOrderDate());
        Order.setIsDeleted(0);
        Order.setOpUserId(adminUserId);
        Order.setTotalPrice(priceTotal);
        Order.setCreateTime(new Date());
        Order.setUpdateTime(new Date());
        mallOrderMapper.insert(Order);

        //2。创建购物清单
        List<OrderItem> OrderItems = new ArrayList<>();
        for (OrderItemVO orderItemVO : saveOrderParam.getOrderItemVOS()) {
            OrderItem OrderItem = new OrderItem();
            BeanUtil.copyProperties(orderItemVO, OrderItem);
            OrderItem.setOrderId(Order.getOrderId());
            OrderItem.setCreateTime(new Date());
            OrderItems.add(OrderItem);
        }
        //保存至数据库
        if (orderItemMapper.insertBatch(OrderItems) > 0 ) {
            //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.OPERATE_ERROR.getResult();
    }

    private Integer countTodayOrder(Date orderDate){
        Date startTime = DateUtils.getDayBeginTime(orderDate);
        Date endTime = DateUtils.getDayEndTime(orderDate);
        LambdaQueryWrapper<MallOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(MallOrder::getCreateTime,startTime);
        queryWrapper.lt(MallOrder::getCreateTime,endTime);
        return mallOrderMapper.selectCount(queryWrapper);
    }
    @Override
    public String updateOrderInfo(Long adminUserId, OrderDetailParam saveOrderParam) {
        MallOrder Order = mallOrderMapper.selectByPrimaryKey(saveOrderParam.getOrderId());
        if (Order == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        //总价
        BigDecimal priceTotal = BigDecimal.ZERO;
        for (OrderItemVO OrderItemVO : saveOrderParam.getOrderItemVOS()) {
            priceTotal = priceTotal.add( OrderItemVO.getSellingPrice().multiply(BigDecimal.valueOf(OrderItemVO.getGoodsCount())) );
        }

        Order.setUserId(saveOrderParam.getUserId());
        Order.setOrderStatus(saveOrderParam.getOrderStatus());
        Order.setExpressStatus(saveOrderParam.getExpressStatus());
        Order.setRemark(saveOrderParam.getRemark());
        Order.setOrderDate(saveOrderParam.getOrderDate());
        Order.setIsDeleted(0);
        Order.setOpUserId(adminUserId);
        Order.setTotalPrice(priceTotal);
        Order.setUpdateTime(new Date());
        mallOrderMapper.insert(Order);

        //删除之前的购物清单
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(OrderItem::getOrderId,saveOrderParam.getOrderId());
        orderItemMapper.delete(queryWrapper);
        //2。创建购物清单
        List<OrderItem> OrderItems = new ArrayList<>();
        for (OrderItemVO orderItemVO : saveOrderParam.getOrderItemVOS()) {
            OrderItem OrderItem = new OrderItem();
            BeanUtil.copyProperties(orderItemVO, OrderItem);
            OrderItem.setOrderId(Order.getOrderId());
            OrderItem.setCreateTime(new Date());
            OrderItems.add(OrderItem);
        }
        //保存至数据库
        if (orderItemMapper.insertBatch(OrderItems) > 0 ) {
            //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.OPERATE_ERROR.getResult();
    }


    @Override
    public PageResult getOrdersPage(PageQueryUtil pageUtil) {
        List<MallOrder> Orders = mallOrderMapper.findOrderList(pageUtil);
        for (MallOrder order : Orders){
           User user = userMapper.selectById( order.getUserId());
            order.setUserName(user.getUserName());
            order.setContactName(user.getContactName());
            order.setUserLevelStr( UserLevelEnum.getUserLevelEnumByStatus(user.getUserLevel()).getName());
            order.setExpressStatusStr(ExpressStatusEnum.getExpressStatusEnumByStatus(order.getExpressStatus()).getName());
            order.setOrderStatusStr(OrderStatusEnum.getOrderStatusEnumByStatus(order.getOrderStatus()).getName());
        }
        int total = mallOrderMapper.getTotalOrders(pageUtil);
        PageResult pageResult = new PageResult(Orders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }


    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrder> orders = mallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrder Order : orders) {
                // isDeleted=1 一定为已关闭订单
                if (Order.getIsDeleted() == 1) {
                    errorOrderNos += Order.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (Order.getOrderStatus() == 4 || Order.getOrderStatus() < 0) {
                    errorOrderNos += Order.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (mallOrderMapper.closeOrder(Arrays.asList(ids), OrderStatusEnum.ORDER_CLOSED.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<OrderItemVO> getOrderItems(Long orderId) {
        MallOrder Order = mallOrderMapper.selectByPrimaryKey(orderId);
        if (Order != null) {
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(Order.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<OrderItemVO> OrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
                return OrderItemVOS;
            }
        }
        return null;
    }
}
