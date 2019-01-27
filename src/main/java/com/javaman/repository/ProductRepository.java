package com.javaman.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.javaman.entity.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

}
