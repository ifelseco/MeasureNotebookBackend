package com.javaman.service;

import java.util.List;

import com.javaman.entity.Order;
import com.javaman.entity.OrderLine;
import com.javaman.model.AddOrderLineDetailListModel;
import com.javaman.model.CalculationResultModel;
import com.javaman.model.OrderLineDetailModel;

public interface OrderLineService {

	List<OrderLine> findAll();

	List<OrderLine> findByOrder(Order order);

	OrderLine findOne(Long id);

	void remove(OrderLine orderLine);
	
	void remove(List<OrderLine> orderLines);

	OrderLine save(OrderLine orderLine);
	
	List<OrderLine> save(List<OrderLine> orderLineList);

	OrderLine update(OrderLine orderLine);
	
	CalculationResultModel calculateTotalAmount(AddOrderLineDetailListModel addOrderLineDetailListModel);
}
