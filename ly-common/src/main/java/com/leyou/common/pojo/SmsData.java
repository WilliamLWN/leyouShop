package com.leyou.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 短信数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsData implements Serializable {
    private String phone;
    private String code;
}
