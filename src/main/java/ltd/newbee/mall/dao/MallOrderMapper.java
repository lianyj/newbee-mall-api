
package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.newbee.mall.entity.MallOrder;
import ltd.newbee.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallOrderMapper extends BaseMapper<MallOrder> {

    MallOrder selectByPrimaryKey(Long orderId);

    MallOrder selectByOrderNo(String orderNo);

    List<MallOrder> findOrderList(PageQueryUtil pageUtil);

    int getTotalOrders(PageQueryUtil pageUtil);

    List<MallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

}