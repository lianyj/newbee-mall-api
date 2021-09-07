
package ltd.newbee.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.newbee.mall.api.mall.param.OrderDetailParam;
import ltd.newbee.mall.api.mall.param.OrderItemParam;
import ltd.newbee.mall.api.mall.vo.OrderDetailVO;
import ltd.newbee.mall.api.mall.vo.OrderItemVO;
import ltd.newbee.mall.entity.MallOrder;
import ltd.newbee.mall.entity.OrderItem;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.Result;

import javax.servlet.http.HttpServletResponse;
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
     * 导出列表
     * @param pageUtil
     * @return
     */
    void exportOrdersList(PageQueryUtil pageUtil, HttpServletResponse response) throws Exception;
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

    String deleteOrder(Long orderId);

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
     * @return
     */
    String closeOrder(Long id);

    List<OrderItemVO> getOrderItems(Long orderId);

    String deleteItem(Long itemId);

    OrderItem getItemDetail(Long itemId);

    String updateItem(Long adminUserId, OrderItemParam orderItemParam);

    String addItem(Long adminUserId, OrderItemParam orderItemParam);

    String changeOrderStatus(Long orderId,Integer orderStatus);

    String changeExpressStatus(Long orderId,Integer expressStatus);


}
