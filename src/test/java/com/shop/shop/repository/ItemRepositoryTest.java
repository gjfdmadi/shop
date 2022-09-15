package com.shop.shop.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.shop.constant.ItemSellStatus;
import com.shop.shop.entity.Item;
import com.shop.shop.entity.QItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

//통합 테스트를 위해 스프링부트에서 제공하는 어노테이션
//실제 애플리케이션을 구동할 때처럼 모든 Bean을 IoC컨테이너에 등록함.
@SpringBootTest
//--------------------------------------------------------
//테스트 코드 실행시 application.properties에 설정해둔 값보다
//application-test.properties에 같은설정이 있다면 더 높은 우선순위를 부여.
@TestPropertySource(locations = "classpath:application-test.properties")
//--------------------------------------------------------
class ItemRepositoryTest {
    //영속성 컨텍스트를 사용하기 위해 @PersistenceContext어노테이션을 이용해 EntityManager 빈 주입
    @PersistenceContext
    EntityManager em;
    //---------------------------------------------

    //ItemRepository를 사용하기 위해서 @Autowired 어노테이션을 이용하여 Bean을 주입
    @Autowired
    ItemRepository itemRepository;
    //----------------------------------------------------
    
    @Test //테스트 할 메소드 위에 선언하여 해당 메소드를 테스트 대상으로 지정함.
    //----------------------------------------------------

    //Junit5에 추가된 어노테이션으로 테스트 코드 실행시 @DisplayName에 지정한 테스트명 노출
    @DisplayName("상품 저장 테스트")
    //----------------------------------------------------

    public void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    //테스트 코드 실행시 데이터베이스에 상품데이터가 없으니 더미데이터 10개 생성
    public void createItemList(){
        for (int i = 1; i <= 10; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }
    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        this.createItemTest();

        //itemRepository 인터페이스에 작성했던 findByItemNm 메소드 호출.
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        //----------------------------------------------
        for (Item item : itemList) {
            System.out.println(item.toString());//조회 결과 얻은 item 객체들을 출력
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();//기존에 만들었던 테스트 상품을 마드는 메소드를 실행해 조회할 대상 만들기

        //상품명이 "테스트 상품1" 또는 상품 상세 설명이 "테스트 상품 상세 설명5" 이면
        //해당 상품을 itemList에 할당함.
        List<Item> itemList =
                itemRepository.findByItemNmOrItemDetail("테스트상품1","테스트 상품 상세 설명5");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        this.createItemList();

        //가격이 10005보다 작은 4개 출력
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createItemList();
        List<Item> itemList =
                itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findItemByItemDetail("테스트 상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

//    @Test
//    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
//    public void findByItemDetailByNative() {
//        this.createItemList();
//        List<Item> itemList =
//                itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
//        for (Item item : itemList) {
//            System.out.println(item.toString());
//        }
//    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        this.createItemList();
        /*JPAQueryFactory를 이용하여 쿼리를 동적으로 생성합니다.
          생성자의 파라미터로는 EntityManager 객체를 넣어줍니다.*/
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //-----------------------------------------------

        QItem qItem = QItem.item;//Querydsl을통해 쿼리를 생성하기위해 플러그인으로 QItem객체 이용

        JPAQuery<Item> query = queryFactory.selectFrom(qItem)//자바 소스코드지만 sql과 비슷하게 소스작성가능
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%"+"테스트 상품 상세 설명"+"%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();//JPAQuery메소드인 fetch를 이용해 쿼리결과를 리스트로 반환

        for (Item item : itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){//상품 데이터를 만드는 새로운 메소드
        for (int i = 1;i <= 5;i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        for (int i = 6;i <= 10;i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){
        this.createItemList2();

        //BooleanBuilder는 쿼리에 들어갈 조건을 만들어주는 빌더
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        //필요한 상품을 조회하는데 필요한 and 조건을 추가하고있음.
        booleanBuilder.and(item.itemDetail.like("%"+itemDetail+"%"));
        booleanBuilder.and(item.price.gt(price));

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }


        //데이터를 페이징해 조회하도록 PageRequest.of() 메소드를 이용해 Pageble 객체생성
        Pageable pageable = PageRequest.of(0,5);

        /*QueryDslPredicateExecutor 인터페이스에서 정의한 findAll()메소드를 이용해
          조건에맞는 데이터를 Page 객체로 받아옴*/
        Page<Item> itemPagingResult =
                itemRepository.findAll(booleanBuilder, pageable);

        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item resultItem: resultItemList) {
            System.out.println(resultItem.toString());
        }
    }
}