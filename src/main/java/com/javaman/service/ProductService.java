package com.javaman.service;

import java.util.List;

import com.javaman.entity.Product;

public interface ProductService {

	List<Product> findAll();

	Product findOne(Long id);

	void remove(Long id);

	Product save(Product roduct);

	Product update(Product roduct);

}
