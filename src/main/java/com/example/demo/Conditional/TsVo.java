package com.example.demo.Conditional;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@JsonPropertyOrder(value = {"abc","legend","yAxis","series"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TsVo implements Serializable {

   private String legend = "{}";
   private String abc = "abc";
   private yAxis yAxis;
   private List<Series> series = new ArrayList<>();

   public void addSeries(Series series){
       this.series.add(series);
   }

    @Data
   static class yAxis {
       private String type;
       private Object[] data;
    }
    @Data
   static class Series{
       private String name;
       private String type;
       private Object data[];
    }

}
