package com.javaman.repository.impl;

import com.javaman.entity.Tenant;
import com.javaman.model.ReportDetailModel;
import com.javaman.model.ReportSummaryModel;
import com.javaman.repository.OrderSummaryReportRepository;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class OrderSummaryReportRepositoryImpl implements OrderSummaryReportRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public ReportSummaryModel getMonthOfYearReport(int year, Tenant tenant) {
        Long id=tenant.getId();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        reportSummaryModel.setReportDetailModelList(new ArrayList<>());
        String sql="select year(orders.order_date),month(orders.order_date),week(orders.order_date),coalesce(sum(orders.total_amount),0) ,count(orders.id) from orders where orders.tenant_id="+id+" and orders.order_status != 'OFFER' and year(orders.order_date)="+year+ " group by month(orders.order_date)";
        Query query=entityManager.createNativeQuery(sql);
        List<Object[]> resultList=query.getResultList();
        mapObjectToModelWeek(reportSummaryModel, resultList);
        return reportSummaryModel;
    }

    @Override
    public ReportSummaryModel getWeekOfMonthReport(int year ,int month, Tenant tenant) {
        Long id=tenant.getId();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        reportSummaryModel.setReportDetailModelList(new ArrayList<>());
        String sql="select year(orders.order_date),month(orders.order_date),week(orders.order_date),coalesce(sum(orders.total_amount),0) ,count(orders.id) from orders where orders.tenant_id="+id+" and orders.order_status != 'OFFER' and month(orders.order_date)="+month+" and year(orders.order_date)="+year+" group by week(orders.order_date)";
        Query query=entityManager.createNativeQuery(sql);
        List<Object[]> resultList=query.getResultList();
        mapObjectToModelWeek(reportSummaryModel, resultList);
        return reportSummaryModel;
    }

    @Override
    public ReportSummaryModel getLastThreeMonthReport(Tenant tenant) {
        Long id=tenant.getId();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        reportSummaryModel.setReportDetailModelList(new ArrayList<>());
        String sql="select year(orders.order_date) , month(orders.order_date) , coalesce(sum(orders.total_amount),0) , count(*) from orders where orders.tenant_id="+id+" and orders.order_status != 'OFFER' and orders.order_date >= date_sub(curdate(),interval 3 month) and month(orders.order_date) <= month(curdate()) group by month(orders.order_date)";
        Query query=entityManager.createNativeQuery(sql);
        List<Object[]> resultList=query.getResultList();
        for(Object[] objectArray:resultList){
            ReportDetailModel reportDetailModel=new ReportDetailModel();
            reportDetailModel.setYear((Integer) objectArray[0]);
            reportDetailModel.setMonth((Integer) objectArray[1]);
            reportDetailModel.setSum(((BigDecimal) objectArray[2]).doubleValue());
            reportDetailModel.setCount(((BigInteger) objectArray[3]).intValue());
            reportSummaryModel.getReportDetailModelList().add(reportDetailModel);
        }
        return reportSummaryModel;
    }

    @Override
    public ReportSummaryModel getEndOfDayReport(Tenant tenant) {
        Long id=tenant.getId();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        reportSummaryModel.setReportDetailModelList(new ArrayList<>());
        String sql="SELECT coalesce(sum(orders.total_amount),0),count(*) FROM orders where orders.tenant_id="+id+" and orders.order_status != 'OFFER' and date(orders.order_date) = curdate()";
        Query query=entityManager.createNativeQuery(sql);
        List<Object[]> resultList=query.getResultList();
        mapOnlySumCountArrayToModel(reportSummaryModel, resultList);

        return reportSummaryModel;
    }

    @Override
    public ReportSummaryModel getLastSevenDayOrder(Tenant tenant) {
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        reportSummaryModel.setReportDetailModelList(new ArrayList<>());
        Long id=tenant.getId();
        String sql="select year(orders.order_date),month(orders.order_date) ,day(orders.order_date),coalesce(sum(orders.total_amount),0),count(*) from orders where orders.tenant_id="+id+" and orders.order_status != 'OFFER' and date(orders.order_date) between date_sub(curdate(),interval 7 day) and curdate() group by date(orders.order_date)";
        Query query=entityManager.createNativeQuery(sql);
        List<Object[]> resultList=query.getResultList();
        mapObjectToModelDay(reportSummaryModel,resultList);
        return reportSummaryModel;
    }

    @Override
    public ReportSummaryModel getLastThirtyDayOrder(Tenant tenant) {
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        reportSummaryModel.setReportDetailModelList(new ArrayList<>());
        Long id=tenant.getId();
        String sql="select year(orders.order_date),month(orders.order_date),day(orders.order_date),coalesce(sum(orders.total_amount),0),count(*) from orders where orders.tenant_id="+id+" and orders.order_status != 'OFFER' and date(orders.order_date) between date_sub(curdate(),interval 30 day) and curdate() group by day(orders.order_date)";
        Query query=entityManager.createNativeQuery(sql);
        List<Object[]> resultList=query.getResultList();
        mapObjectToModelDay(reportSummaryModel,resultList);
        return reportSummaryModel;
    }

    @Override
    public ReportSummaryModel getAllGiroByDate(Tenant tenant, Date startDate , Date endDate) {
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        reportSummaryModel.setReportDetailModelList(new ArrayList<>());
        Long id=tenant.getId();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String mStartDate=sdf.format(startDate);
        String mEndDate=sdf.format(endDate);
        String sql="SELECT coalesce(sum(orders.total_amount),0),count(*) FROM orders where orders.tenant_id="+id+" and orders.order_status != 'OFFER' and date(orders.order_date) between '"+mStartDate+"' and '"+mEndDate+"'";
        Query query=entityManager.createNativeQuery(sql);
        List<Object[]> resultList=query.getResultList();
        mapOnlySumCountArrayToModel(reportSummaryModel, resultList);
        return reportSummaryModel;
    }

    private void mapOnlySumCountArrayToModel(ReportSummaryModel reportSummaryModel, List<Object[]> resultList) {
        for (Object[] objectArray:resultList) {
            ReportDetailModel reportDetailModel=new ReportDetailModel();
            reportDetailModel.setSum(((BigDecimal) objectArray[0]).doubleValue());
            reportDetailModel.setCount(((BigInteger) objectArray[1]).intValue());
            reportSummaryModel.getReportDetailModelList().add(reportDetailModel);
        }
    }

    private void mapObjectToModelWeek(ReportSummaryModel reportSummaryModel, List<Object[]> resultList) {
        for(Object[] objectArray:resultList){
            ReportDetailModel reportDetailModel=new ReportDetailModel();
            reportDetailModel.setYear((Integer) objectArray[0]);
            reportDetailModel.setMonth((Integer) objectArray[1]);
            reportDetailModel.setWeek((Integer) objectArray[2]);
            reportDetailModel.setSum(((BigDecimal) objectArray[3]).doubleValue());
            reportDetailModel.setCount(((BigInteger) objectArray[4]).intValue());
            reportSummaryModel.getReportDetailModelList().add(reportDetailModel);
        }
    }

    private void mapObjectToModelDay(ReportSummaryModel reportSummaryModel, List<Object[]> resultList) {
        for(Object[] objectArray:resultList){
            ReportDetailModel reportDetailModel=new ReportDetailModel();
            reportDetailModel.setYear((Integer) objectArray[0]);
            reportDetailModel.setMonth((Integer) objectArray[1]);
            reportDetailModel.setDay((Integer) objectArray[2]);
            reportDetailModel.setSum(((BigDecimal) objectArray[3]).doubleValue());
            reportDetailModel.setCount(((BigInteger) objectArray[4]).intValue());
            reportSummaryModel.getReportDetailModelList().add(reportDetailModel);
        }
    }


}
