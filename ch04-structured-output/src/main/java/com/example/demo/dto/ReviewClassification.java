package com.example.demo.dto;

import lombok.Data;

@Data
public class ReviewClassification {
  // ##### 열거 타입 선언 #####
  public enum Sentiment {
    POSITIVE, //긍정적 
    NEUTRAL,  //중립적
    NEGATIVE  //부정적
  }

  // ##### 필드 선언 #####
  private String review;
  private Sentiment classification;
}
