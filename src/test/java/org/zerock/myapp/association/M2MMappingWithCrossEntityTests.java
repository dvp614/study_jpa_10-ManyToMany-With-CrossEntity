package org.zerock.myapp.association;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.zerock.myapp.entity.Orders;
import org.zerock.myapp.entity.Product4;
import org.zerock.myapp.entity.Shopper4;
import org.zerock.myapp.util.PersistenceUnits;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class M2MMappingWithCrossEntityTests {
	private EntityManagerFactory emf;
	private EntityManager em;

	@BeforeAll
	void beforeAll() {
		log.trace("beforeAll() invoked.");

		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.H2);

		this.em = this.emf.createEntityManager();
		assertNotNull(this.em);
	} // beforeAll

	@AfterAll
	void afterAll() {
		log.trace("afterAll() invoked.");

		this.em.clear();
		try {this.em.close();} catch (Exception _ignored) {}
		try {this.emf.close();} catch (Exception _ignored) {}
	} // afterAll

//	@Disabled
	@Order(1)
	@Test
//	@RepeatedTest(1)
	@DisplayName("1. prepareData")
	@Timeout(value = 1L, unit = TimeUnit.MINUTES)
	void prepareData() {
		log.trace("prepareData invoked.");

		IntStream.rangeClosed(1, 7).forEachOrdered(seq -> {

			try {
				this.em.getTransaction().begin();

				// 엔티티.....
				Shopper4 transientProduct = new Shopper4();
				transientProduct.setName("NAME-" + seq);

				this.em.persist(transientProduct);

				this.em.getTransaction().commit();
			} catch (Exception e) {
				this.em.getTransaction().rollback();

				throw e;
			} // try-catch
		}); // forEachOrdered
		
		IntStream.rangeClosed(1, 3).forEachOrdered(seq -> {
			try {
				this.em.getTransaction().begin();
				
				
				Product4 transientProduct = new Product4();
				transientProduct.setName("NAME_" + seq);
				
				this.em.persist(transientProduct);
				
				this.em.getTransaction().commit();
			}catch(Exception e) {
				this.em.getTransaction().rollback();
				throw e;
			} // try-catch
		}); // .forEachOrdered
		
		IntStream.rangeClosed(1, 30).forEachOrdered(seq -> {
			Orders transientOrder = new Orders();
			
			int shopperId = new Random().nextInt(1, 8);	// half-open
			Shopper4 foundShopper = 
				this.em.<Shopper4>find(Shopper4.class, 0L+shopperId);	// 주문고객(무작위)
			
			Objects.requireNonNull(foundShopper);
			transientOrder.setShopperFK(foundShopper);
			
			// ---------------
			
			int productId = new Random().nextInt(1, 4);	// half-open
			Product4 foundProduct = 
				this.em.find(Product4.class, 0L+productId);	// 주문상품(무작위)
			
			Objects.requireNonNull(foundProduct);
			transientOrder.setProductFK(foundProduct);
			
			
			// ---------------
			
			transientOrder.setOrderAmount(new Random().nextInt(101));		// 주문수량(무작위)
			transientOrder.setOrderPrice(new Random().nextInt(100001));		// 주문가격(무작위)
			
			try {
				this.em.getTransaction().begin();
				
				
				foundShopper.order(transientOrder);
				
				this.em.persist(transientOrder);		// 주문내역 저장
				this.em.getTransaction().commit();
			} catch(Exception e) {
				this.em.getTransaction().rollback();
				throw e;
			} // try-catch
		}); // .forEachOrdered
		
		
		log.info("\t+ Done.");

	} // prepareData

//	@Disabled
	@Order(2)
	@Test
//	@RepeatedTest(1)
	@DisplayName("2. testObjectGraph1")
	@Timeout(value = 5L, unit = TimeUnit.MINUTES)
	void testObjectGraph1() {
		log.trace("testObjectGraph1 invoked.");
	
		int shopperId = new Random().nextInt(1, 8);	// half-open
		Shopper4 foundShopper = this.em.<Shopper4>find(Shopper4.class, 0L+shopperId);
		
		assert foundShopper != null;
		log.info("\t+ foundShopper: {}", foundShopper);
		
		foundShopper.getMyOrders().forEach(o -> log.info(o.toString()));
	} // testObjectGraph1
	
//	@Disabled
	@Order(3)
	@Test
//	@RepeatedTest(1)
	@DisplayName("3. testObjectGraphTraverseFromProduct4ToOrders")
	@Timeout(value=1L, unit = TimeUnit.MINUTES)
	void testObjectGraphTraverseFromProduct4ToOrders() {	// Traverse : Product4 -> Orders (1 : M)
		log.trace("testObjectGraphTraverseFromProduct4ToOrders() invoked.");
		
		int productId = new Random().nextInt(1, 4);	// half-open
		Product4 foundProduct = this.em.<Product4>find(Product4.class, 0L+productId);
		
		assert foundProduct != null;
		log.info("\t+ foundProduct: {}", foundProduct);
		
		foundProduct.getMyOrder().forEach(o -> log.info(o.toString()));
		// 이 이상의 정보를 알 수는 없다!!! 왜? 
		// Children 이 Product4 Entity 에 없기 때문에...
	} // testObjectGraphTraverseFromProduct4ToOrders
	
//	@Disabled
	@Order(4)
	@Test
//	@RepeatedTest(1)
	@DisplayName("4. testObjectGraphTraverseOfOrders")
	@Timeout(value=1L, unit = TimeUnit.MINUTES)
	void testObjectGraphTraverseOfOrders() {	// Traverse : Orders
		log.trace("testObjectGraphTraverseOfOrders() invoked.");
		
		int orderId = new Random().nextInt(1, 31);	// half-open
		Orders foundOrder = this.em.<Orders>find(Orders.class, 0L+orderId);
		
		assert foundOrder != null;
		log.info("\t+ foundOrder: {}", foundOrder);
		
		// 이 이상의 정보를 알 수는 없다!!! 왜? 
		// Children 이 Shopper4 Entity 에 없기 때문에...
	} // testObjectGraphTraverseOfOrders
	
	
//	@Disabled
	@Order(5)
	@Test
//	@RepeatedTest(1)
	@DisplayName("5. testObjectGraphTraverseFinal")
	@Timeout(value=1L, unit = TimeUnit.MINUTES)
	void testObjectGraphTraverseFinal() {	// Traverse : Orders
		log.trace("testObjectGraphTraverseFinal() invoked.");
		
		// 이제는 Shopper4와 Product4에 Children(Many): List<CrossEntity> 
		// 컬렉션이 있기 때문에, 하나의 shopper or product 를 찾았을 때에,
		// 유용한 주문내역목록 정보를 모두 얻을 수 있다!!
		
	} // testObjectGraphTraverseFinal
} // end class
