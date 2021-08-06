
package ltd.newbee.mall.common;

import lombok.Getter;

/**
 * @apiNote 用户等级
 */
@Getter
public enum UserLevelEnum {

    A(1, "A"),
    B(2, "B"),
    C(3, "C");

    private int userLevel;

    private String name;

    UserLevelEnum(int userLevel, String name) {
        this.userLevel = userLevel;
        this.name = name;
    }

    public static UserLevelEnum getUserLevelEnumByStatus(int userLevel) {
        for (UserLevelEnum userLevelEnum : UserLevelEnum.values()) {
            if (userLevelEnum.getUserLevel() == userLevel) {
                return userLevelEnum;
            }
        }
        return null;
    }


}
