package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity(name = "Orders")
@Table(name = "orders")
public class Orders implements Serializable{
	@Serial private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orders_id")
	private Long id;
	
	@Basic(optional = false)
	private Integer orderAmount;
	
	@Basic(optional = false)
	private Integer orderPrice;
	
	@CreationTimestamp		// 자동으로 시간 생성
	@Basic(optional = false)
	private Date orderDate;	
	
	@ManyToOne(targetEntity = Shopper4.class)
	@JoinColumn(name = "shopper_id")
	private Shopper4 shopperFK;
	
	@ManyToOne(targetEntity = Product4.class)
	@JoinColumn(name = "product_id")
	private Product4 productFK;
	
} // end class
