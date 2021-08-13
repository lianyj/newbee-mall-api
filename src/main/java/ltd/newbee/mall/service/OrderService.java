
package ltd.newbee.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.newbee.mall.api.mall.param.OrderDetailParam;
import ltd.newbee.mall.api.mall.param.OrderItemParam;
import ltd.newbee.mall.api.mall.vo.OrderDetailVO;
import ltd.newbee.mall.api.mall.vo.OrderItemVO;
import ltd.newbee.mall.entity.MallOrder;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface OrderService extends IService<MallOrder> {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getOrdersPage(PageQueryUtil pageUtil);


    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    OrderDetailVO getOrderDetailByOrderId(Long orderId,Long adminUserId);


    /**
     * 创建订单信息
     * @return
     */
    String saveOrder(Long adminUserId, OrderDetailParam saveOrderParam);



    /**
     * 订单信息修改
     *
     * @return
     */
    String updateOrderInfo(Long adminUserId, OrderDetailParam saveOrderParam);

    String editOrderInfo(Long adminUserId, OrderDetailParam saveOrderParam);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long id);

    List<OrderItemVO> getOrderItems(Long orderId);

    String deleteItem(Long itemId);

    String updateItem(Long adminUserId, OrderItemParam orderItemParam);

    String addItem(Long adminUserId, OrderItemParam orderItemParam);

    String changeOrderStatus(Long orderId,Integer orderStatus);

    String changeExpressStatus(Long orderId,Integer expressStatus);


}
