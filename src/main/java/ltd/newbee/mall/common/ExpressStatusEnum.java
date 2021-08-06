package ltd.newbee.mall.common;

/**
 * ClassName ExpressStatusEnum
 * Description
 * Create by yanjie14
 * Date 2021/8/4 7:51 下午
 */
public enum ExpressStatusEnum {

    NOT_PUSH(1, "未寄出"),
    HAVE_PUSH(2, "已寄出"),;

    private int payStatus;

    private String name;

    ExpressStatusEnum(int payStatus, String name) {
        this.payStatus = payStatus;
        this.name = name;
    }

    public static ExpressStatusEnum getExpressStatusEnumByStatus(int payStatus) {
        for (ExpressStatusEnum expressStatusEnum : ExpressStatusEnum.values()) {
            if (expressStatusEnum.getPayStatus() == payStatus) {
                return expressStatusEnum;
            }
        }
        return NOT_PUSH;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
