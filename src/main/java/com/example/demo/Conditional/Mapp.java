package com.example.demo.Conditional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface Mapp {

    @Select("select * from org")
    List<Po> getData();

}
