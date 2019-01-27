package com.javaman.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanySummaryModel {

    private BaseModel baseModel;
    private List<CompanyDetailModel> companies=new ArrayList<>();

}
