package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
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

@Data
@Entity(name = "Product4")
@Table(name = "product4")
public class Product4 implements Serializable{
	@Serial private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;
	
	@Basic(optional = false)
	private String name;
	
	@OneToMany(mappedBy = "productFK", targetEntity = Orders.class)
	@ToString.Exclude
	private List<Orders> myOrder = new Vector<>();
} // end class
