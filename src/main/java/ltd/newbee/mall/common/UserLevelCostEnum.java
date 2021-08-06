
package ltd.newbee.mall.common;

import lombok.Getter;

/**
 * @apiNote 用户折扣
 */
@Getter
public enum UserLevelCostEnum {

    COST_A(1, 0.85),
    COST_B(2, 0.80),
    COST_C(3, 0.90);

    private int userLevel;

    private double value;

    UserLevelCostEnum(int userLevel, double value) {
        this.userLevel = userLevel;
        this.value = value;
    }

    public static UserLevelCostEnum getUserLevelEnumByStatus(int userLevel) {
        for (UserLevelCostEnum userLevelCostEnum : UserLevelCostEnum.values()) {
            if (userLevelCostEnum.getUserLevel() == userLevel) {
                return userLevelCostEnum;
            }
        }
        return null;
    }


}
