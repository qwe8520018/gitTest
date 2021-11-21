package com.example.demo.Conditional;


import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

@Data
public class Po {
    long id;
    String org;
    String cyt;
    String yty;
    String type;
}
