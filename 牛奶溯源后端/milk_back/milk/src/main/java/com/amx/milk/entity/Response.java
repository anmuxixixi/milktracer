package com.amx.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Response implements Serializable {

    private int code;   // 返回的状态码
    private String message;  // 返回的备注信息
    private Object data;  // 返回的数据信息

}
