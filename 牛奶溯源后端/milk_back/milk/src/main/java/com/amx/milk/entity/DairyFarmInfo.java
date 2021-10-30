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
public class DairyFarmInfo {

    private String id;
    private Date create_time;
    private int max_Cow_No;
    private int max_Bucket_No;
    private String name;

}
