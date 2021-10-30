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
public class CowOperation {

    private String cowId;
    private Date operateTime;
    private String operation;
    private String consumptionOrOutput;

}
