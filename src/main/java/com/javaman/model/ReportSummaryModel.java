package com.javaman.model;

import lombok.Data;

import java.util.List;

@Data
public class ReportSummaryModel {

    private BaseModel baseModel;
    private List<ReportDetailModel> reportDetailModelList;
}
