package com.javaman.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.javaman.entity.ProducNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaman.entity.Order;
import com.javaman.entity.OrderLine;
import com.javaman.model.AddOrderLineDetailListModel;
import com.javaman.model.CalculationResultModel;
import com.javaman.model.OrderLineDetailModel;
import com.javaman.repository.OrderLineRepository;
import com.javaman.repository.OrderRepository;
import com.javaman.service.OrderLineService;
import com.javaman.util.Calculate;
import com.javaman.util.CalculationResult;

@Service
public class OrderLineServiceImpl implements OrderLineService {

	@Autowired
	private OrderLineRepository orderLineRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public List<OrderLine> findAll() {
		ArrayList<OrderLine> orderLines;
		orderLines = (ArrayList<OrderLine>) orderLineRepository.findAll();

		return orderLines;
	}

	@Override
	public List<OrderLine> findByOrder(Order order) {

		ArrayList<OrderLine> orderLines;
		orderLines = (ArrayList<OrderLine>) orderLineRepository.findByOrder(order);

		return orderLines;

	}

	@Override
	public OrderLine findOne(Long id) {
		// TODO Auto-generated method stub
		return orderLineRepository.findOne(id);
	}

	@Override
	public void remove(OrderLine orderLine) {
		
		Order order=orderLine.getOrder();
		BigDecimal currenTotalAmount;
		BigDecimal newTotalAmount;
		
		currenTotalAmount=order.getTotalAmount();
		newTotalAmount=currenTotalAmount.subtract(orderLine.getLineAmount());
		order.setTotalAmount(newTotalAmount);
		if(isTailorOrderLine(orderLine)){
			int count=order.getTailorOrderLineCount();
			count--;
			order.setTailorOrderLineCount(count);
		}
		orderRepository.save(order);
		orderLineRepository.delete(orderLine);

	}

	@Override
	public OrderLine save(OrderLine orderLine) {

		// calculate used material
		// calculate line amount

		int productCode = orderLine.getProduct().getProductValue().ordinal();
		OrderLine mOrderLine=null;
		Calculate calculate = new Calculate();

		Order order = orderRepository.findOne(orderLine.getOrder().getId());

		BigDecimal totalAmount = order.getTotalAmount();
		int tailorOrderLineCout=order.getTailorOrderLineCount();

		if(orderLine.getId()!=null){
			mOrderLine=orderLineRepository.findOne(orderLine.getId());
		}



		if (order != null) {

			switch (productCode) {
			case 0:
				CalculationResult resultNetCurt = calculate.calcNetCurt(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());

				setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultNetCurt);

				break;
			case 1:
				CalculationResult resultSunBlind = calculate.calcSunBlind(orderLine.getPropertyWidth(),
						orderLine.getUnitPrice());

				setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultSunBlind);

				break;
			case 2:
				CalculationResult resultRollerBlind = calculate.calcRollerZebraVerticalBlind(
						orderLine.getPropertyWidth(), orderLine.getPropertyHeight(), orderLine.getUnitPrice());

				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultRollerBlind);

				break;
			case 3:
				CalculationResult resultZebra = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());

				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultZebra);

				break;
			case 4:
				CalculationResult resultJalouise = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());

				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultJalouise);

				break;
			case 5:
				CalculationResult resultVertical = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());

				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultVertical);

				break;
			case 6:
				CalculationResult resultNetDouble = calculate.calcDoubleNetCurt(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());

				setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultNetDouble);


				break;
			case 7:
				CalculationResult resultBriz = calculate.calcBriz(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());

				setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultBriz);


				break;
			case 8:

				setFarbelaCurtain(orderLine, order, totalAmount, tailorOrderLineCout, mOrderLine);

				break;
			case 9:

				int fonCode = orderLine.getFonType().ordinal();

				tailorOrderLineCout++;
				order.setTailorOrderLineCount(tailorOrderLineCout);

				if (fonCode == 1) {
					CalculationResult resultKruvazeKanat = calculate.calcKruvazeFon(orderLine.getPropertyWidth(),
							orderLine.getSizeOfPile(), orderLine.getUnitPrice());

					setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultKruvazeKanat);

					break;
				} else if (fonCode == 2) {
					CalculationResult resultFonKanat = calculate.calcFonKanat(orderLine.getPropertyWidth(),
							orderLine.getSizeOfPile(), orderLine.getUnitPrice());

					setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultFonKanat);

					break;
				} else if(fonCode==3){
					CalculationResult resultJapanPanel = calculate.calcJapanPanel(orderLine.getPropertyWidth(),
							orderLine.getUnitPrice());

					setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultJapanPanel);

					break;
				}

			case 10:
				CalculationResult resultNetRollerBlind = calculate.calcRollerZebraVerticalBlind(
						orderLine.getPropertyWidth(), orderLine.getPropertyHeight(), orderLine.getUnitPrice());

				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultNetRollerBlind);

				break;

			default:
				break;
			}
		}

		// TODO Auto-generated method stub
		return orderLineRepository.save(orderLine);
	}


	@Override
	public OrderLine update(OrderLine orderLine) {
		// TODO Auto-generated method stub
		return orderLineRepository.save(orderLine);
	}

	@Override
	public List<OrderLine> save(List<OrderLine> orderLineList) {

		ArrayList<OrderLine> orderLines = new ArrayList<>();

		for (OrderLine orderLine : orderLineList) {

			int productCode = orderLine.getProduct().getProductValue().ordinal();
			Calculate calculate = new Calculate();
			Order order = orderRepository.findOne(orderLine.getOrder().getId());
			BigDecimal totalAmount = order.getTotalAmount();
			int tailorOrderLineCout=order.getTailorOrderLineCount();
			OrderLine mOrderLine=null;
			if(orderLine.getId()!=null){
				mOrderLine=orderLineRepository.findOne(orderLine.getId());
			}

			switch (productCode) {
			case 0:
				CalculationResult resultNetCurt = calculate.calcNetCurt(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());
				setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultNetCurt);
				orderLines.add(orderLine);
				break;
			case 1:
				CalculationResult resultSunBlind = calculate.calcSunBlind(orderLine.getPropertyWidth(),
						orderLine.getUnitPrice());
				setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultSunBlind);
				orderLines.add(orderLine);
				break;
			case 2:
				CalculationResult resultRollerBlind = calculate.calcRollerZebraVerticalBlind(
						orderLine.getPropertyWidth(), orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultRollerBlind);
				orderLines.add(orderLine);
				break;
			case 3:
				CalculationResult resultZebra = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultZebra);
				orderLines.add(orderLine);
				break;
			case 4:
				CalculationResult resultJalouise = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultJalouise);
				orderLines.add(orderLine);
				break;
			case 5:
				CalculationResult resultVertical = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultVertical);
				orderLines.add(orderLine);
				break;
			case 6:
				CalculationResult resultNetDouble = calculate.calcDoubleNetCurt(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());
				setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultNetDouble);
				orderLines.add(orderLine);
				break;
			case 7:
				CalculationResult resultBriz = calculate.calcBriz(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());
				setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultBriz);
				orderLines.add(orderLine);
				break;
			case 8:

				setFarbelaCurtain(orderLine, order, totalAmount, tailorOrderLineCout, mOrderLine);
				orderLines.add(orderLine);
				break;
			case 9:
				int fonCode = orderLine.getFonType().ordinal();
				tailorOrderLineCout++;
				order.setTailorOrderLineCount(tailorOrderLineCout);
				if (fonCode == 1) {
					CalculationResult resultKruvazeKanat = calculate.calcKruvazeFon(orderLine.getPropertyWidth(),
							orderLine.getSizeOfPile(), orderLine.getUnitPrice());
					setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultKruvazeKanat);
					orderLines.add(orderLine);
					break;
				} else if (fonCode == 2) {
					CalculationResult resultFonKanat = calculate.calcFonKanat(orderLine.getPropertyWidth(),
							orderLine.getSizeOfPile(), orderLine.getUnitPrice());
					setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultFonKanat);
					orderLines.add(orderLine);
					break;
				} else {
					CalculationResult resultJapanPanel = calculate.calcJapanPanel(orderLine.getPropertyWidth(),
							orderLine.getUnitPrice());
					setTailorCurtain(orderLine, mOrderLine, order, totalAmount, tailorOrderLineCout, resultJapanPanel);
					orderLines.add(orderLine);
					break;
				}

			case 10:
				CalculationResult resultNetRollerBlind = calculate.calcRollerZebraVerticalBlind(
						orderLine.getPropertyWidth(), orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				setMechanismCurtain(orderLine, mOrderLine, order, totalAmount, resultNetRollerBlind);
				orderLines.add(orderLine);
				break;
			default:
				break;
			}

		}

		return (List<OrderLine>) orderLineRepository.save(orderLines);
	}

	private void setFarbelaCurtain(OrderLine orderLine, Order order, BigDecimal totalAmount, int tailorOrderLineCout, OrderLine mOrderLine) {
		if(mOrderLine!=null){
            totalAmount=totalAmount.subtract(mOrderLine.getLineAmount()).add(orderLine.getLineAmount());
            order.setTotalAmount(totalAmount);
            orderLine.setOrder(order);
        }else{
            tailorOrderLineCout++;
            order.setTailorOrderLineCount(tailorOrderLineCout);
            totalAmount = totalAmount.add(orderLine.getLineAmount());
            order.setTotalAmount(totalAmount);
            orderLine.setOrder(order);
        }
	}

	@Override
	public CalculationResultModel calculateTotalAmount(AddOrderLineDetailListModel addOrderLineDetailListModel) {


		BigDecimal totalAmount = new BigDecimal(0);
		BigDecimal usedMaterial = new BigDecimal(0);
		CalculationResultModel calculationResultModel=new CalculationResultModel();
		Calculate calculate = new Calculate();
		

		for (OrderLineDetailModel orderLine : addOrderLineDetailListModel.getOrderLineDetailModelList()) {

			int productCode = orderLine.getProduct().getProductValue().ordinal();
			
			switch (productCode) {
			case 0:
				CalculationResult resultNetCurt = calculate.calcNetCurt(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultNetCurt.getUsedMaterial());
				totalAmount=totalAmount.add(resultNetCurt.getLineAmount());
				break;
			case 1:
				CalculationResult resultSunBlind = calculate.calcSunBlind(orderLine.getPropertyWidth(),
						orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultSunBlind.getUsedMaterial());
				totalAmount=totalAmount.add(resultSunBlind.getLineAmount());
				break;
			case 2:
				CalculationResult resultRollerBlind = calculate.calcRollerZebraVerticalBlind(
						orderLine.getPropertyWidth(), orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultRollerBlind.getUsedMaterial());
				totalAmount=totalAmount.add(resultRollerBlind.getLineAmount());
				break;
			case 3:
				CalculationResult resultZebra = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultZebra.getUsedMaterial());
				totalAmount=totalAmount.add(resultZebra.getLineAmount());
				break;
			case 4:
				CalculationResult resultJalouise = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultJalouise.getUsedMaterial());
				totalAmount=totalAmount.add(resultJalouise.getLineAmount());
				break;
			case 5:
				CalculationResult resultVertical = calculate.calcRollerZebraVerticalBlind(orderLine.getPropertyWidth(),
						orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultVertical.getUsedMaterial());
				totalAmount=totalAmount.add(resultVertical.getLineAmount());
				break;
			case 6:
				CalculationResult resultNetDouble = calculate.calcDoubleNetCurt(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultNetDouble.getUsedMaterial());
				totalAmount=totalAmount.add(resultNetDouble.getLineAmount());
				break;
			case 7:
				CalculationResult resultBriz = calculate.calcBriz(orderLine.getPropertyWidth(),
						orderLine.getSizeOfPile(), orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultBriz.getUsedMaterial());
				totalAmount=totalAmount.add(resultBriz.getLineAmount());
			case 8:
				break;
			case 9:
				int fonCode = orderLine.getFonType().ordinal();
				if (fonCode == 1) {
					CalculationResult resultKruvazeKanat = calculate.calcKruvazeFon(orderLine.getPropertyWidth(),
							orderLine.getSizeOfPile(), orderLine.getUnitPrice());
					usedMaterial=usedMaterial.add(resultKruvazeKanat.getUsedMaterial());
					totalAmount=totalAmount.add(resultKruvazeKanat.getLineAmount());
					break;
				} else if (fonCode == 2) {
					CalculationResult resultFonKanat = calculate.calcFonKanat(orderLine.getPropertyWidth(),
							orderLine.getSizeOfPile(), orderLine.getUnitPrice());
					usedMaterial=usedMaterial.add(resultFonKanat.getUsedMaterial());
					totalAmount=totalAmount.add(resultFonKanat.getLineAmount());
					break;
				} else {
					CalculationResult resultJapanPanel = calculate.calcJapanPanel(orderLine.getPropertyWidth(),
							orderLine.getUnitPrice());
					usedMaterial=usedMaterial.add(resultJapanPanel.getUsedMaterial());
					totalAmount=totalAmount.add(resultJapanPanel.getLineAmount());
					break;
				}

			case 10:
				CalculationResult resultNetRollerBlind = calculate.calcRollerZebraVerticalBlind(
						orderLine.getPropertyWidth(), orderLine.getPropertyHeight(), orderLine.getUnitPrice());
				usedMaterial=usedMaterial.add(resultNetRollerBlind.getUsedMaterial());
				totalAmount=totalAmount.add(resultNetRollerBlind.getLineAmount());
				break;
			default:
				break;
			}

		}
		calculationResultModel.setTotalAmount(totalAmount.doubleValue());
		calculationResultModel.setUsedMaterial(usedMaterial.doubleValue());
		return calculationResultModel;
		
	}

	@Override
	public void remove(List<OrderLine> orderLines) {
		
		BigDecimal currenTotalAmount;
		BigDecimal newTotalAmount;
		
		for (OrderLine orderLine : orderLines) {
			Order order=orderLine.getOrder();
			currenTotalAmount=order.getTotalAmount();
			newTotalAmount=currenTotalAmount.subtract(orderLine.getLineAmount());
			order.setTotalAmount(newTotalAmount);
			if(isTailorOrderLine(orderLine)){
				int count=order.getTailorOrderLineCount();
				count--;
				order.setTailorOrderLineCount(count);
			}
			orderRepository.save(order);
		}
		
		orderLineRepository.delete(orderLines);
	}

	public boolean isTailorOrderLine(OrderLine orderLine){
		ProducNames productValue=orderLine.getProduct().getProductValue();
		ArrayList<ProducNames> listOfTailorProduct=new ArrayList<>();
		listOfTailorProduct.addAll(Arrays.asList(ProducNames.TUL,ProducNames.GUNESLIK,
				ProducNames.KRUVAZETUL,ProducNames.BRIZ,ProducNames.FARBELA,ProducNames.FON));

		if(listOfTailorProduct.contains(productValue)){
			return true;
		}else{
			return false;
		}

	}

	private void setTailorCurtain(OrderLine orderLine, OrderLine mOrderLine, Order order, BigDecimal totalAmount, int tailorOrderLineCout, CalculationResult calcResultTailor) {
		if(mOrderLine!=null){
			totalAmount=totalAmount.subtract(mOrderLine.getLineAmount()).add(calcResultTailor.getLineAmount()) ;
			order.setTotalAmount(totalAmount);
			orderLine.setLineAmount(calcResultTailor.getLineAmount());
			orderLine.setUsedMaterial(calcResultTailor.getUsedMaterial());
			orderLine.setOrder(order);
		}else{
			orderLine.setLineAmount(calcResultTailor.getLineAmount());
			orderLine.setUsedMaterial(calcResultTailor.getUsedMaterial());
			tailorOrderLineCout++;
			order.setTailorOrderLineCount(tailorOrderLineCout);
			totalAmount =totalAmount.add(calcResultTailor.getLineAmount());
			order.setTotalAmount(totalAmount);
			orderLine.setOrder(order);
		}
	}

	private void setMechanismCurtain(OrderLine orderLine, OrderLine mOrderLine, Order order, BigDecimal totalAmount, CalculationResult calcResultMechanism) {
		if(mOrderLine!=null){
			totalAmount=totalAmount.subtract(mOrderLine.getLineAmount()).add(calcResultMechanism.getLineAmount());
			order.setTotalAmount(totalAmount);
			orderLine.setOrder(order);
			orderLine.setLineAmount(calcResultMechanism.getLineAmount());
			orderLine.setUsedMaterial(calcResultMechanism.getUsedMaterial());
		}else{
			orderLine.setLineAmount(calcResultMechanism.getLineAmount());
			orderLine.setUsedMaterial(calcResultMechanism.getUsedMaterial());
			totalAmount =totalAmount.add(calcResultMechanism.getLineAmount());
			order.setTotalAmount(totalAmount);
			orderLine.setOrder(order);
		}
	}

}
