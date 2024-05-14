package edu.shu.abs.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    NORMAL_USER(0, "normal_user"),
    ADMIN(1, "admin");

    @EnumValue
    private final Integer key;

    @JsonValue
    private final String value;

    /**
     * 根据key获取枚举类
     */
    public static Role getRole(Integer key) {
        for (Role role : values()) {
            if (role.getKey().equals(key))
                return role;
        }
        return null;
    }

    /**
     * 根据display获取枚举类
     */
    public static Role getRole(String value) {
        for (Role role : values()) {
            if (role.getValue().equals(value))
                return role;
        }
        return null;
    }

    Role(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}


