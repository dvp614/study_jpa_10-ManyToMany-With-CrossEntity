package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Entity(name = "Shopper4")
@Table(name = "shopper4")
public class Shopper4 implements Serializable{
	@Serial private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shopper_id")
	private Long id;
	
	@Basic(optional = false)
	private String name;
	
	@OneToMany(mappedBy = "shopperFK", targetEntity = Orders.class)
	@ToString.Exclude
	private List<Orders> myOrders = new Vector<>();
	
	public boolean order(Orders newOrder) {
		log.trace("order({}) invoked.", newOrder);
		
		if(Objects.nonNull(newOrder.getProductFK())) return false;
		if(newOrder.getOrderAmount() < 1) return false;
		if(newOrder.getOrderPrice() < 1) return false;
		
		
		Orders transientOrder = new Orders();
		transientOrder.setOrderDate(new Date());
		
		return this.myOrders.add(newOrder);
	} // order
} // end class
