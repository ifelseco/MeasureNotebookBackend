package com.javaman.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import com.javaman.entity.*;
import com.javaman.util.ConstRole;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javaman.model.AddOrderLineDetailListModel;
import com.javaman.model.AddOrderLineListResultModel;
import com.javaman.model.AddOrderLineResultModel;
import com.javaman.model.BaseModel;
import com.javaman.model.CalculationResultModel;
import com.javaman.model.DeleteOrderLinesModel;
import com.javaman.model.OrderLineDetailModel;
import com.javaman.service.OrderLineService;
import com.javaman.service.OrderService;
import com.javaman.service.UserService;

@RestController
@RequestMapping("/orderLine")
@Secured({ "ROLE_ADMIN", "ROLE_SUPER_ADMIN","ROLE_USER"  })
public class OrderLineController {
	
	private static final Logger LOG = LoggerFactory.getLogger(OrderLineController.class);
	private OrderService orderService;
	private UserService userService;
	private OrderLineService orderLineService;
	private ModelMapper modelMapper;

	@Autowired
	public OrderLineController(OrderService orderService,UserService userService,
							   OrderLineService orderLineService,ModelMapper modelMapper) {
		this.modelMapper=modelMapper;
		this.orderLineService=orderLineService;
		this.orderService=orderService;
		this.userService=userService;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<AddOrderLineResultModel> addOrderLine(@RequestBody OrderLineDetailModel orderLineDetailModel,
			Principal principal) {

		AddOrderLineResultModel addOrderLineResultModel = new AddOrderLineResultModel();
		BaseModel baseModel = new BaseModel();


		try {

			User user = userService.findByUsername(principal.getName());

			Tenant tenant = user.getTenant();

			Order order = orderService.findOne(orderLineDetailModel.getOrder().getId());

			if (order != null) {

				if(orderLineDetailModel.getId()!=null){
					//orderline update

					OrderLine orderLine = orderLineService.findOne(orderLineDetailModel.getId());

					if(orderLine!=null){
						// totalAmount ve lineAmount servis katmanında güncellenecek.

						orderLine = modelMapper.map(orderLineDetailModel, OrderLine.class);
						orderLine.setLineAmount(new BigDecimal(orderLineDetailModel.getLineAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
						orderLine.setUsedMaterial(new BigDecimal(orderLineDetailModel.getUsedMaterial()).setScale(2,BigDecimal.ROUND_HALF_UP));

						LOG.info("OrderLine {}", orderLine.toString());

						orderLine.getProduct().setTenant(tenant);
						orderLine.setOrder(order);
						orderLine.setTenant(tenant);

						orderLine = orderLineService.save(orderLine);

						addOrderLineResultModel = modelMapper.map(orderLine, AddOrderLineResultModel.class);
						addOrderLineResultModel.setLineAmount(orderLine.getLineAmount().doubleValue());
						addOrderLineResultModel.setOrderDepositeAmount(orderLine.getOrder().getDepositeAmount().doubleValue());
						addOrderLineResultModel.setOrderTotalAmount(orderLine.getOrder().getTotalAmount().doubleValue());

						baseModel.setResponseCode(0);
						baseModel.setResponseMessage("Sipariş başarıyla güncellendi.");
						addOrderLineResultModel.setBaseModel(baseModel);

						return new ResponseEntity<>(addOrderLineResultModel, HttpStatus.OK);
					}else{
						baseModel.setResponseCode(1);
						baseModel.setResponseMessage("Hata: Sipariş bulunamadı.");
						addOrderLineResultModel.setBaseModel(baseModel);

						return new ResponseEntity<>(addOrderLineResultModel, HttpStatus.NOT_FOUND);
					}


				}else{
					//new orderline add
					OrderLine orderLine = new OrderLine();

					// totalAmount ve lineAmount servis katmanında güncellenecek.

					orderLine = modelMapper.map(orderLineDetailModel, OrderLine.class);
					orderLine.setLineAmount(new BigDecimal(orderLineDetailModel.getLineAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
					orderLine.setUsedMaterial(new BigDecimal(orderLineDetailModel.getUsedMaterial()).setScale(2,BigDecimal.ROUND_HALF_UP));

					LOG.info("OrderLine {}", orderLine.toString());

					orderLine.getProduct().setTenant(tenant);
					orderLine.setOrder(order);
					orderLine.setTenant(tenant);

					orderLine = orderLineService.save(orderLine);

					addOrderLineResultModel = modelMapper.map(orderLine, AddOrderLineResultModel.class);
					addOrderLineResultModel.setLineAmount(orderLine.getLineAmount().doubleValue());
					addOrderLineResultModel.setOrderDepositeAmount(orderLine.getOrder().getDepositeAmount().doubleValue());
					addOrderLineResultModel.setOrderTotalAmount(orderLine.getOrder().getTotalAmount().doubleValue());

					baseModel.setResponseCode(0);
					baseModel.setResponseMessage("Sipariş başarıyla eklendi.");
					addOrderLineResultModel.setBaseModel(baseModel);

					return new ResponseEntity<>(addOrderLineResultModel, HttpStatus.OK);
				}



			} else {
				baseModel.setResponseCode(1);
				baseModel.setResponseMessage("Bu id de sipariş yok, yeni sipariş kaydı aç.");
				addOrderLineResultModel.setBaseModel(baseModel);
				return new ResponseEntity<>(addOrderLineResultModel, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			LOG.error("Orderline Add Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Hata: Ölçü ekleme sırasında hata oluştu.");
			addOrderLineResultModel.setBaseModel(baseModel);

			return new ResponseEntity<>(addOrderLineResultModel, HttpStatus.BAD_REQUEST);
		}

	}
	
	@RequestMapping(value = "/list/add", method = RequestMethod.POST)
	public ResponseEntity<AddOrderLineListResultModel> addOrderLineList(
			@RequestBody AddOrderLineDetailListModel orderLineDetailListModel, Principal principal) {

		AddOrderLineListResultModel addOrderLineListResultModel = new AddOrderLineListResultModel();
		BaseModel baseModel = new BaseModel();
		//ModelMapper modelMapper = new ModelMapper();
		List<OrderLine> orderLines = new ArrayList<>();

		try {
			User user = userService.findByUsername(principal.getName());
			Tenant tenant = user.getTenant();

			for (OrderLineDetailModel orderLineDetailModel : orderLineDetailListModel.getOrderLineDetailModelList()) {
				Order order = orderService.findOne(orderLineDetailModel.getOrder().getId());
				if (order != null) {
					OrderLine orderLine = new OrderLine();
					// totalAmount ve lineAmount servis katmanında güncellenecek.
					orderLine = modelMapper.map(orderLineDetailModel, OrderLine.class);
					orderLine.setLineAmount(new BigDecimal(orderLineDetailModel.getLineAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
					orderLine.setUsedMaterial(new BigDecimal(orderLineDetailModel.getUsedMaterial()).setScale(2,BigDecimal.ROUND_HALF_UP));
					orderLine.getProduct().setTenant(tenant);
					orderLine.setTenant(tenant);
					orderLine.setOrder(order);
					orderLines.add(orderLine);
				}

			}
			if (orderLines.size() > 0) {
				List<OrderLine> orderLinesSaved = orderLineService.save(orderLines);
				BigDecimal orderTotalAmount = orderLinesSaved.get(0).getOrder().getTotalAmount();

				for (OrderLine orderLine : orderLinesSaved) {

					OrderLineDetailModel orderLineDetailModel ;
					orderLineDetailModel = modelMapper.map(orderLine, OrderLineDetailModel.class);
					orderLineDetailModel.setLineAmount(orderLine.getLineAmount().doubleValue());
					addOrderLineListResultModel.getOrderLines().add(orderLineDetailModel);
				}
				addOrderLineListResultModel.setOrderTotalAmount(orderTotalAmount.doubleValue());
				baseModel.setResponseCode(0);
				baseModel.setResponseMessage("Sipariş ekleme işlemi başarılı.");
				addOrderLineListResultModel.setBaseModel(baseModel);
				return new ResponseEntity<>(addOrderLineListResultModel, HttpStatus.OK);

			} else {
				baseModel.setResponseCode(1);
				baseModel.setResponseMessage("Parçalı sipariş eklerken hata oluştu bilgilerinizi kontrol ediniz.");
				addOrderLineListResultModel.setBaseModel(baseModel);
				return new ResponseEntity<>(addOrderLineListResultModel,
						HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error("Orderline List Add Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Hata: Ölçü ekleme sırasında hata oluştu.");
			addOrderLineListResultModel.setBaseModel(baseModel);

			return new ResponseEntity<>(addOrderLineListResultModel, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@Secured(ConstRole.ARG_ADMIN_ROLE)
	public ResponseEntity<BaseModel> deleteOrderLine(@PathVariable long id) {

		BaseModel baseModel = new BaseModel();

		try {

			OrderLine orderLine = orderLineService.findOne(id);

			if (orderLine != null) {

				orderLineService.remove(orderLine);
				baseModel.setResponseCode(0);
				baseModel.setResponseMessage("Silme işlemi başarılı.");
				return new ResponseEntity<>(baseModel, HttpStatus.OK);
			} else {
				baseModel.setResponseCode(1);
				baseModel.setResponseMessage("Silmek istediğiniz sipariş ögesi kayıtlarda yok.");
				return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error("Orderline Delete Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Hata: Silme işlemi sırasında bir hata oluştu.");
			return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.DELETE)
	@Secured(ConstRole.ARG_ADMIN_ROLE)
	public ResponseEntity<BaseModel> deleteOrderLines(@RequestBody DeleteOrderLinesModel deleteOrderLinesModel,
			Principal principal) {

		BaseModel baseModel = new BaseModel();
		ArrayList<OrderLine> orderLines = new ArrayList<>();
		try {
			User user=userService.findByUsername(principal.getName());
			if(user!=null){
				for (Long id : deleteOrderLinesModel.getOrderLineIds()) {
					OrderLine orderLine = orderLineService.findOne(id);
					if (orderLine != null) {
						orderLines.add(orderLine);
					}
				}

				if (orderLines.size() > 0) {
					orderLineService.remove(orderLines);
					baseModel.setResponseCode(0);
					baseModel.setResponseMessage("Seçilen siparişler başarıyla silindi.");
					return new ResponseEntity<>(baseModel, HttpStatus.OK);
				} else {
					baseModel.setResponseCode(0);
					baseModel.setResponseMessage("Seçilen siparişler zaten  silinmiş.");
					return new ResponseEntity<>(baseModel, HttpStatus.OK);
				}
			}else{
				baseModel.setResponseCode(1);
				baseModel.setResponseMessage("Kullanıcı bulunamadı tekrar giriş yapınız.");
				return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error("Orderline Delete List Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Hata: Sipariş silme sırasında hata oluştu.");
			return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/calculate", method = RequestMethod.POST)
	public ResponseEntity<CalculationResultModel> calculate(
			@RequestBody AddOrderLineDetailListModel orderLineDetailListModel, Principal principal) {

		BaseModel baseModel = new BaseModel();
		CalculationResultModel calculationResultModel = new CalculationResultModel();

		try {

			if (orderLineDetailListModel.getOrderLineDetailModelList().size() > 0) {
				calculationResultModel = orderLineService.calculateTotalAmount(orderLineDetailListModel);
				baseModel.setResponseCode(0);
				baseModel.setResponseMessage("Sipariş ekleme işlemi başarılı.");
				calculationResultModel.setBaseModel(baseModel);
				return new ResponseEntity<>(calculationResultModel, HttpStatus.OK);

			} else {
				baseModel.setResponseCode(1);
				baseModel.setResponseMessage("Hesap için eksik veri gönderdiniz, kontrol ediniz.");
				calculationResultModel.setBaseModel(baseModel);
				return new ResponseEntity<>(calculationResultModel, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error("Orderline Calculate Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Ölçü ekleme sırasında hata oluştu.");
			calculationResultModel.setBaseModel(baseModel);

			return new ResponseEntity<>(calculationResultModel, HttpStatus.BAD_REQUEST);
		}

	}
}
