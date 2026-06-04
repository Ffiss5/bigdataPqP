package com.itfan.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfo {
    private int id;
    private String name;
    private String ing_head;
    private String email;
    private int months;
    private int days;
}
