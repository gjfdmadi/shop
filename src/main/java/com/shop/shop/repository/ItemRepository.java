package com.shop.shop.repository;

import com.shop.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ItemRepository extends JpaRepository<Item, Long>,
QuerydslPredicateExecutor<Item>{//QueryDslPredicateExecutor 인터페이스 상속 추가
    //itemNm(상품명)으로 데이터를 조회하기 위해서 By뒤에 필드명인 ItemNm을 메소드의 이름에 붙여줍니다.
    //엔티티명은 생략이 가능하므로 findItemByItemNm 대신에 findByItemNm으로 메소드명을 만들어줍니다.
    //매개 변수로는 검색할 때 상품명 변수를 넘겨줍니다.
    List<Item> findByItemNm(String itemNm);
    //---------------------------------------------

    //상품을 상품명과 상품 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메소드.
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    //---------------------------------------------

    //파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터를 조회하는 쿼리 메소드.
    List<Item> findByPriceLessThan(Integer price);
    //---------------------------------------------

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    //from 뒤에는 엔티티 클래스로 작성한 Item 지정. Item으로부터 데이터 select
    @Query("select i from Item i where i.itemDetail like " +
            "%:itemDetail% order by i.price desc")
    //@Param어노테이션 이용하여 파라미터로 넘어온값을 JPQL에 들어갈 변수로 지정
    //itemDetail 변수를 "like % %" 사이에 ".itemDetail"로 값이 들어가도록 작성
    List<Item> findItemByItemDetail(@Param("itemDetail") String itemDetail);
    //---------------------------------------------

    //value 안에 네이티브 쿼리문을 작성하고 "nativeQuery = true"를 지정.
//    @Query(value = "select * from item i where i.item_detail like" +
//            "%:itemDetail% order by i.price desc ", nativeQuery = true)
//    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
