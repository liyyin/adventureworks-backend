package com.refresh.customer_api.repository;

import com.refresh.customer_api.dto.ProductSelectorResponse;
import com.refresh.customer_api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query(value = """
            SELECT * FROM Production.Product
            WHERE (Name LIKE CONCAT('%',:keyword,'%')
                OR ProductNumber LIKE CONCAT('%', :keyword, '%'))
            """,
            countQuery = """
            SELECT COUNT(*) FROM Production.Product
            WHERE (Name LIKE CONCAT('%',:keyword,'%')
                OR ProductNumber LIKE CONCAT('%',:keyword,'%'))
            """
            ,nativeQuery = true)
    Page<Product> searchByKeyWordNative(@Param("keyword")String keyword, Pageable pageable);

    @Query("select p from Product p order by p.productNumber")
    List<Product> findForSelect();

    List<ProductSelectorResponse> findAllByOrderByNameDesc();

    boolean existsByName(String name);
    boolean existsByProductNumber(String productNumber);
    //ini untuk update jadi cek duplikat dan cek id dia
    boolean existsByNameAndIdNot(String name, Integer id);
    boolean existsByProductNumberAndIdNot(String productNumber, Integer id);

}