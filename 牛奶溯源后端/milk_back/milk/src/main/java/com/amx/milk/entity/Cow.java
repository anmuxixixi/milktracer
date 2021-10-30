package com.amx.milk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cow {

    private String id;
    private String farmId;
    private boolean healthy;
    private boolean quarantine;
    private String feedSource;
    private int status;

}
