
package ltd.newbee.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.newbee.mall.api.mall.param.OrderDetailParam;
import ltd.newbee.mall.api.mall.param.OrderItemParam;
import ltd.newbee.mall.api.mall.vo.OrderDetailVO;
import ltd.newbee.mall.api.mall.vo.OrderItemVO;
import ltd.newbee.mall.common.*;
import ltd.newbee.mall.common.Exception;
import ltd.newbee.mall.dao.*;
import ltd.newbee.mall.entity.*;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder> implements OrderService {

    @Resource
    private MallOrderMapper mallOrderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private GoodsInfoMapper goodsInfoMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public OrderDetailVO getOrderDetailByOrderId(Long orderId,Long adminUserId) {
        MallOrder order = mallOrderMapper.selectById(orderId);
        if (order == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        User user = userMapper.selectById(order.getUserId());
        if (user == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        OrderDetailVO OrderDetailVO = new OrderDetailVO();
        //订单信息
        OrderDetailVO.setOrderNo(order.getOrderNo());
        OrderDetailVO.setOrderDate(order.getOrderDate());
        OrderDetailVO.setExpressFee(order.getExpressFee());
        OrderDetailVO.setRemark(order.getRemark());
        OrderDetailVO.setOrderId(order.getOrderId());
        OrderDetailVO.setPayType(order.getPayType());
        //客户信息
        OrderDetailVO.setUserId(user.getUserId());
        OrderDetailVO.setUserName(user.getUserName());
        OrderDetailVO.setContactName(user.getContactName());
        OrderDetailVO.setMobile(user.getMobile());
        OrderDetailVO.setAddress(user.getAddress());
        OrderDetailVO.setCostRate(UserLevelCostEnum.getUserLevelEnumByStatus(user.getUserLevel()).getValue());

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(orderItems)) {
            //商品信息
            List<OrderItemVO> OrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
            //合计
            Integer goodsCount = 0 ;
            BigDecimal originalPrice = BigDecimal.ZERO ;
            BigDecimal sellingPrice = BigDecimal.ZERO ;
            for (OrderItemVO orderItemVO:OrderItemVOS){
                goodsCount = goodsCount +orderItemVO.getGoodsCount();
                originalPrice = originalPrice.add(orderItemVO.getOriginalPrice().multiply(BigDecimal.valueOf(orderItemVO.getGoodsCount())));
                sellingPrice = sellingPrice.add(orderItemVO.getSellingPrice().multiply(BigDecimal.valueOf(orderItemVO.getGoodsCount())));
            }
            OrderItemVO orderItemVO = new OrderItemVO();
            orderItemVO.setGoodsName("合计");
            orderItemVO.setGoodsCount(goodsCount);
            orderItemVO.setOriginalPrice( originalPrice.add(BigDecimal.valueOf(order.getExpressFee())));
            orderItemVO.setSellingPrice(sellingPrice.add(BigDecimal.valueOf(order.getExpressFee())));
            orderItemVO.setGoodsUnit("");
            OrderItemVOS.add(orderItemVO);
            OrderDetailVO.setOrderItemVOS(OrderItemVOS);
        }

        if(adminUserId != null){
            AdminUser adminUser = adminUserMapper.selectById(adminUserId);
            OrderDetailVO.setPrintName(adminUser.getNickName());
        }
        OrderDetailVO.setPrintTime(DateUtils.getCurrentDate());
        return OrderDetailVO;
    }


    @Override
    public String deleteOrder(Long orderId){
        MallOrder order = mallOrderMapper.selectById(orderId);
        if (order == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        order.setIsDeleted(1);
        mallOrderMapper.updateById(order);
        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public String saveOrder(Long adminUserId, OrderDetailParam saveOrderParam) {
        User user = userMapper.selectById(saveOrderParam.getUserId());
        if (user == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        //1.创建订单
        MallOrder Order = new MallOrder();
        //用户等级 + 付款方式 + yyymmdd+数量
        String userLevel = UserLevelEnum.getUserLevelEnumByStatus(user.getUserLevel()).getName();
        Integer count = countTodayOrder(saveOrderParam.getOrderDate())+1;
        String number = String.format("%04d", count);
        String orderNo = userLevel + saveOrderParam.getPayType() + DateUtils.format(saveOrderParam.getOrderDate() ,"yyyyMMdd") + number;
        Order.setOrderNo(orderNo);
        Order.setUserId(saveOrderParam.getUserId());
        Order.setOrderStatus(saveOrderParam.getOrderStatus());
        Order.setExpressStatus(saveOrderParam.getExpressStatus());
        Order.setExpressFee(saveOrderParam.getExpressFee());
        Order.setRemark(saveOrderParam.getRemark());
        Order.setOrderDate(saveOrderParam.getOrderDate());
        Order.setPayType(saveOrderParam.getPayType());
        Order.setIsDeleted(0);
        Order.setOpUserId(adminUserId);
        Order.setTotalPrice(BigDecimal.ZERO);
        Order.setCreateTime(new Date());
        Order.setUpdateTime(new Date());
        mallOrderMapper.insert(Order);

        return ServiceResultEnum.SUCCESS.getResult();
    }

    private Integer countTodayOrder(Date orderDate){
        LambdaQueryWrapper<MallOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MallOrder::getOrderDate,orderDate);
        return mallOrderMapper.selectCount(queryWrapper);
    }
    @Override
    public String updateOrderInfo(Long adminUserId, OrderDetailParam saveOrderParam) {
        MallOrder Order = mallOrderMapper.selectById(saveOrderParam.getOrderId());
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
        mallOrderMapper.updateById(Order);

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
    public String editOrderInfo(Long adminUserId, OrderDetailParam saveOrderParam) {
        MallOrder Order = mallOrderMapper.selectById(saveOrderParam.getOrderId());
        if (Order == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        Order.setUserId(saveOrderParam.getUserId());
        Order.setExpressFee(saveOrderParam.getExpressFee());
        Order.setRemark(saveOrderParam.getRemark());
        Order.setOrderDate(saveOrderParam.getOrderDate());
        Order.setOpUserId(adminUserId);
        Order.setUpdateTime(new Date());
        mallOrderMapper.updateById(Order);
        return ServiceResultEnum.SUCCESS.getResult();
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
    public void exportOrdersList(PageQueryUtil pageUtil, HttpServletResponse response) throws java.lang.Exception {
        PageResult pageResult =  getOrdersPage(pageUtil);
        List<MallOrder> orders = pageResult.getList();
        List<Map<String, Object>> dateList = new LinkedList<Map<String, Object>>();
        for (MallOrder order :orders){

            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getOrderId());
            for (OrderItem item :items){
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("orderDate",DateUtils.formatDate(order.getOrderDate()));
                map.put("orderNo",order.getOrderNo());
                map.put("goodInfo",item.getGoodsName() + item.getGoodsIntro());
                map.put("unit","片");
                map.put("goodCount",item.getGoodsCount());
                map.put("originalPrice",item.getOriginalPrice());
                map.put("sellingPrice",item.getSellingPrice());
                map.put("expressFree",order.getExpressFee());
                dateList.add(map);
            }
        }
        //导出
        String[] headerArr = new String[]{"订单日期", "订单编号", "产品名称/型号", "单位", "数量", "标批价", "折后价", "快递费"};
        String sheetName = "复核页数据导出表.xls";
        ExcelUtil.writeToExcel(dateList, headerArr, sheetName, response);

    }

    @Override
    public String closeOrder(Long orderId) {
        //查询所有的订单 判断状态 修改状态和更新时间
        MallOrder mallOrder = mallOrderMapper.selectById(orderId);
        if (mallOrder == null) {
            return "订单不存在";
        }
        mallOrder.setOrderStatus(3);
        mallOrderMapper.updateById(mallOrder);
        //未查询到数据 返回错误提示
        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public List<OrderItemVO> getOrderItems(Long orderId) {
        MallOrder Order = mallOrderMapper.selectById(orderId);
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

    @Override
    @Transactional
    public String deleteItem( Long itemId){
        OrderItem orderItem = orderItemMapper.selectById(itemId);
        if(orderItem == null){
            return "详情不存在";
        }
        orderItemMapper.deleteById(itemId);

        MallOrder mallOrder = mallOrderMapper.selectById(orderItem.getOrderId());
        if (mallOrder == null) {
            return "订单不存在";
        }
        // 总价
        setTotalPrice(mallOrder);
        //未查询到数据 返回错误提示
        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public OrderItem getItemDetail(Long itemId){
        return  orderItemMapper.selectById(itemId);
    }

    public void setTotalPrice(MallOrder mallOrder ){
        // 总价
        List<OrderItem> list = orderItemMapper.selectByOrderId(mallOrder.getOrderId());
        BigDecimal priceTotal = BigDecimal.ZERO;
        for (OrderItem OrderItemVO : list) {
            priceTotal = priceTotal.add( OrderItemVO.getSellingPrice().multiply(BigDecimal.valueOf(OrderItemVO.getGoodsCount())) );
        }
        //加上快递费
        priceTotal =  priceTotal.add(BigDecimal.valueOf( mallOrder.getExpressFee()));
        mallOrder.setTotalPrice(priceTotal);
        mallOrderMapper.updateById(mallOrder);
    }

    @Override
    public String updateItem(Long adminUserId, OrderItemParam orderItemParam){
        OrderItem orderItem = orderItemMapper.selectById(orderItemParam.getOrderItemId());
        if (orderItem == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsInfo goodsInfo = goodsInfoMapper.selectById( orderItemParam.getGoodsId());
        if (goodsInfo == null) {
            return "产品不存在";
        }
       MallOrder mallOrder =  mallOrderMapper.selectById(orderItemParam.getOrderId());
        if (mallOrder == null) {
            return "订单不存在";
        }
        User user = userMapper.selectById(mallOrder.getUserId());
        if (user == null) {
            return "用户不存在";
        }
        orderItem.setGoodsId(orderItemParam.getGoodsId());
        orderItem.setGoodsName(goodsInfo.getGoodsName());
        orderItem.setGoodsCount(orderItemParam.getGoodsCount());
        orderItem.setGoodsIntro(orderItemParam.getGoodsIntro());
        if(!StringUtils.isEmpty(orderItemParam.getOriginalPrice())){
            orderItem.setOriginalPrice(orderItemParam.getOriginalPrice());
        }else {
            orderItem.setOriginalPrice(goodsInfo.getOriginalPrice());
        }
        if(!StringUtils.isEmpty(orderItemParam.getSellingPrice())){
            orderItem.setSellingPrice(orderItemParam.getSellingPrice());
        }else {
            double cost = UserLevelCostEnum.getUserLevelEnumByStatus(user.getUserLevel()).getValue();
            BigDecimal sellingPrice = goodsInfo.getOriginalPrice().multiply(BigDecimal.valueOf(cost));
            orderItem.setSellingPrice(sellingPrice);
        }
        orderItemMapper.updateById(orderItem);
        // 总价
        setTotalPrice(mallOrder);

        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public String addItem(Long adminUserId, OrderItemParam orderItemParam){

        GoodsInfo goodsInfo = goodsInfoMapper.selectById( orderItemParam.getGoodsId());
        if (goodsInfo == null) {
            return "产品不存在";
        }
        MallOrder mallOrder =  mallOrderMapper.selectById(orderItemParam.getOrderId());
        if (mallOrder == null) {
            return "订单不存在";
        }
        User user = userMapper.selectById(mallOrder.getUserId());
        if (user == null) {
            return "用户不存在";
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderItemParam.getOrderId());
        orderItem.setGoodsId(orderItemParam.getGoodsId());
        orderItem.setGoodsName(goodsInfo.getGoodsName());
        orderItem.setGoodsCount(orderItemParam.getGoodsCount());
        orderItem.setGoodsIntro(orderItemParam.getGoodsIntro());
        if(!StringUtils.isEmpty(orderItemParam.getOriginalPrice())){
            orderItem.setOriginalPrice(orderItemParam.getOriginalPrice());
        }else {
            orderItem.setOriginalPrice(goodsInfo.getOriginalPrice());
        }
        if(!StringUtils.isEmpty(orderItemParam.getSellingPrice())){
            orderItem.setSellingPrice(orderItemParam.getSellingPrice());
        }else {
            double cost = UserLevelCostEnum.getUserLevelEnumByStatus(user.getUserLevel()).getValue();
            BigDecimal sellingPrice = goodsInfo.getOriginalPrice().multiply(BigDecimal.valueOf(cost));
            orderItem.setSellingPrice(sellingPrice);
        }
        orderItem.setCreateTime(new Date());
        orderItemMapper.insert(orderItem);
        // 总价
        setTotalPrice(mallOrder);
        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public String changeOrderStatus(Long orderId,Integer orderStatus){
        MallOrder mallOrder = mallOrderMapper.selectById(orderId);
        if (mallOrder == null) {
            return "订单不存在";
        }
        mallOrder.setOrderStatus(orderStatus);
        mallOrderMapper.updateById(mallOrder);
        return ServiceResultEnum.SUCCESS.getResult();
    }

    @Override
    public String changeExpressStatus(Long orderId,Integer expressStatus){
        MallOrder mallOrder = mallOrderMapper.selectById(orderId);
        if (mallOrder == null) {
            return "订单不存在";
        }
        mallOrder.setExpressStatus(expressStatus);
        mallOrderMapper.updateById(mallOrder);
        return ServiceResultEnum.SUCCESS.getResult();
    }



}
