
package ltd.newbee.mall.common;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 * @apiNote 订单状态:0.待支付 1.已支付 2.配货完成 3:出库成功 4.交易成功 -1.手动关闭 -2.超时关闭 -3.商家关闭
 */
public enum OrderStatusEnum {

    ORDER_PRE_PAY(1, "待支付"),
    ORDER_PAID(2, "已支付"),
    ORDER_CLOSED(3, "已关闭"),;



    private int orderStatus;

    private String name;

    OrderStatusEnum(int orderStatus, String name) {
        this.orderStatus = orderStatus;
        this.name = name;
    }

    public static OrderStatusEnum getOrderStatusEnumByStatus(int orderStatus) {
        for (OrderStatusEnum OrderStatusEnum : OrderStatusEnum.values()) {
            if (OrderStatusEnum.getOrderStatus() == orderStatus) {
                return OrderStatusEnum;
            }
        }
        return ORDER_PRE_PAY;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
