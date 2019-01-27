package com.javaman.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaman.entity.Product;
import com.javaman.repository.ProductRepository;
import com.javaman.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<Product> findAll() {

		ArrayList<Product> products = new ArrayList<Product>();
		products = (ArrayList<Product>) productRepository.findAll();

		return products;
	}

	@Override
	public Product findOne(Long id) {
		// TODO Auto-generated method stub
		return productRepository.findOne(id);
	}

	@Override
	public void remove(Long id) {

		productRepository.delete(id);

	}

	@Override
	public Product save(Product product) {
		// TODO Auto-generated method stub

		return productRepository.save(product);
	}

	@Override
	public Product update(Product product) {
		// TODO Auto-generated method stub
		return productRepository.save(product);

	}

}
