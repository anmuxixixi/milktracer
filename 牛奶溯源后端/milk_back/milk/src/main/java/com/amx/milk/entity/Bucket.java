package com.amx.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Bucket {

    private String id;
    private String machiningId;
    private Date time;
    private Date inMachiningTime;
    private String status;

}
