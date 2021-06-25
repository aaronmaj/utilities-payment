package com.utility.payments.repository;

import com.utility.payments.obr.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {

    Pageable pageable = new PageRequest(0, 10, Sort.Direction.DESC, "id");

    @Query(value = "SELECT next_val from hibernate_sequence  limit 1", nativeQuery = true)
    Long getNextId();

    public Payment getByReferenceNo(String referenceNo);

    public Payment getById(Long id);

    @Query("select payment from Payment payment order by payment.paymentDate  desc ")
    List<Payment> findAllPayments();

    List<Payment> findPaymentByPaymentDateLessThanEqual(Date date);

    List<Payment> findPaymentByPaymentDateGreaterThanEqual(Date date);

    List<Payment> findPaymentByPaymentDateBetweenOrderByPaymentDateDesc(Date startDate, Date endDate);

    List<Payment> findPaymentByPaymentDateEqualsOrderByPaymentDateDesc(Date date);

    @Query(
            value = "select p.* from payment p where DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsByExactDate(@Param("date") String date);

    /*
    @Query("select payment from Payment payment order by payment.paymentDate  desc ")
    Page<Payment> findAll(Pageable pageable);
    */
    @EntityGraph(attributePaths = "details")
    List<Payment> findDistinctBy();

    @Query("select p from Payment p left join fetch p.details")
    List<Payment> findWithQuery();

    @Query("select distinct p from Payment p left join fetch p.details")
    List<Payment> findDistinctWithQuery();

    default Page<Payment> findTopPayments() {
        return findAll(pageable);
    }

    @Query(
            value = "select p.* from payment p where p.payment_date >= STR_TO_DATE(:minDate,'%d/%m/%Y') order by p.payment_date desc)", nativeQuery = true)
    List<Payment> findPaymentsByMinDate(@Param("minDate") String minDate);

    @Query(
            value = "select p.* from payment p where p.payment_date <= STR_TO_DATE(:maxDate,'%d/%m/%Y') order by p.payment_date desc)", nativeQuery = true)
    List<Payment> findPaymentsByMaxDate(@Param("maxDate") String maxDate);

    @Query(
            value = "select p.* from payment p where DATE(p.payment_date) between :startDate and :endDate order by p.payment_date desc)", nativeQuery = true)
    List<Payment> findPaymentsByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(
            value = "select p.* from payment p where (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%)", nativeQuery = true)
    List<Payment> findPaymentsByKeyWord(@Param("keyWord") String keyWord);

    @Query(
            value = "select p.* from payment p join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where value like %:value%", nativeQuery = true)
    List<Payment> findPaymentsByDetailValue(@Param("value") String value);

    @Query(
            value = "select p.* from payment p join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where f.name=:fieldName and value like %:value%", nativeQuery = true)
    List<Payment> findPaymentsByDetail(@Param("fieldName") String name, @Param("value") String value);

    @Query(
            value = "select p.* from payment p  where (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentsByKeywordAndExactDateValues(@Param("keyWord") String keyword, @Param("date") String date);

    @Query(
            value = "select p.* from payment p  where (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE(p.payment_date) between :startDate and :endDate order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentsByKeywordAndDateValues(@Param("keyWord") String keyword, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(
            value = "select p.* from payment p join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesWithExactDate(@Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value, @Param("date") String date);

    @Query(
            value = "select p.* from payment p join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE(p.payment_date) between :startDate and :endDate and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValues(@Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value, @Param("startDate") String startDate, @Param("endDate") String endDate);


    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo where acc.name = ?1",
            nativeQuery = true)
    List<Payment> findPaymentsByAccount(String accountName);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where acc.name =:account and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE(p.payment_date) between :startDate and :endDate and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesAndAccountName(@Param("account") String account, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where acc.name =:account and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesWithExactDateAndAccountName(@Param("account") String account, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value, @Param("date") String date);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo  where acc.name =:account and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE(p.payment_date) between :startDate and :endDate order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentsByKeywordAndDateValuesAndAccountName(@Param("account") String account, @Param("keyWord") String keyword, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo  join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where acc.name =:account and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesWithoutDateAndAccountName(@Param("account") String account, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo  where acc.name =:account and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentsByKeywordAndExactDateValuesAndAccountName(@Param("account") String account, @Param("keyWord") String keyword, @Param("date") String date);

    List<Payment> findPaymentByAccountNameAndPaymentDateBetweenOrderByPaymentDateDesc(String accountName, Date startDate, Date endDate);

    List<Payment> findPaymentByAccountNameAndPaymentDateLessThanEqual(String accountName, Date date);

    List<Payment> findPaymentByAccountNameAndPaymentDateGreaterThanEqual(String accountName, Date date);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo  where acc.name =:account and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsByAccountNameAndExactDate(@Param("account") String account, @Param("date") String date);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo  where acc.name =:account and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%)", nativeQuery = true)
    List<Payment> findPaymentsByAccountNameAndKeyWord(@Param("account") String account, @Param("keyWord") String keyWord);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where acc.name =:account and f.name=:fieldName and value like %:value%", nativeQuery = true)
    List<Payment> findPaymentsByAccountNameAndDetail(@Param("account") String account, @Param("fieldName") String name, @Param("value") String value);

    @Query(
            value = "select p.* from account acc join payment p on p.accountNo=acc.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where acc.name =:account and value like %:value%", nativeQuery = true)
    List<Payment> findPaymentsByAccountNameAndDetailValue(@Param("account") String account, @Param("value") String value);


    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo where s.name = ?1",
            nativeQuery = true)
    List<Payment> findPaymentsBySubCategory(String subCategoryName);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where s.name =:subCategory and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE(p.payment_date) between :startDate and :endDate and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesAndSubCategoryName(@Param("subCategory") String subCategory, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where s.name =:subCategory and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesWithExactDateAndSubCategoryName(@Param("subCategory") String subCategory, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value, @Param("date") String date);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where s.name =:subCategory and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE(p.payment_date) between :startDate and :endDate order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentsByKeywordAndDateValuesAndSubCategoryName(@Param("subCategory") String subCategory, @Param("keyWord") String keyword, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where s.name =:subCategory and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesWithoutDateAndSubCategoryName(@Param("subCategory") String subCategory, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where s.name =:subCategory and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentsByKeywordAndExactDateValuesAndSubCategoryName(@Param("subCategory") String subCategory, @Param("keyWord") String keyword, @Param("date") String date);
    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where s.name =:subCategory and DATE(p.payment_date) between :startDate and :endDate order by p.payment_date desc ", nativeQuery = true)

    List<Payment> findPaymentBySubCategoryNameAndPaymentDateBetweenOrderByPaymentDateDesc(@Param("subCategory") String subCategory, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where s.name =:subCategory and DATE(p.payment_date) <= :date order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentBySubCategoryNameAndPaymentDateLessThanEqual(String accountName, Date date);
    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where s.name =:subCategory and DATE(p.payment_date) >= :date order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentBySubCategoryNameAndPaymentDateGreaterThanEqual(@Param("subCategory") String subCategory, @Param("date") Date date);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where s.name =:subCategory and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySubCategoryNameAndExactDate(@Param("subCategory") String subCategory, @Param("date") String date);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where s.name =:subCategory and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%)", nativeQuery = true)
    List<Payment> findPaymentsBySubCategoryNameAndKeyWord(@Param("subCategory") String subCategory, @Param("keyWord") String keyWord);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where s.name =:subCategory and f.name=:fieldName and value like %:value%", nativeQuery = true)
    List<Payment> findPaymentsBySubCategoryNameAndDetail(@Param("subCategory") String subCategory, @Param("fieldName") String name, @Param("value") String value);

    @Query(
            value = "select p.* from sub_category s join account a on s.code=a.code join payment p on p.accountNo=a.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where s.name =:subCategory and value like %:value%", nativeQuery = true)
    List<Payment> findPaymentsBySubCategoryNameAndDetailValue(@Param("subCategory") String subCategory, @Param("value") String value);


    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo where c.category_name = ?1",
            nativeQuery = true)
    List<Payment> findPaymentsByCategory(String categoryName);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where c.category_name=:category and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE(p.payment_date) between :startDate and :endDate and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesAndCategoryName(@Param("category") String category, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where c.category_name=:category and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesWithExactDateAndCategoryName(@Param("category") String category, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value, @Param("date") String date);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where c.category_name=:category and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE(p.payment_date) between :startDate and :endDate order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentsByKeywordAndDateValuesAndCategoryName(@Param("category") String category, @Param("keyWord") String keyword, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where c.category_name=:category and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesWithoutDateAndCategoryName(@Param("category") String category, @Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where c.category_name=:category and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentsByKeywordAndExactDateValuesAndCategoryName(@Param("category") String category, @Param("keyWord") String keyword, @Param("date") String date);
    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where c.category_name=:category and DATE(p.payment_date) between :startDate and :endDate order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentByCategoryNameAndPaymentDateBetweenOrderByPaymentDateDesc(@Param("category") String category, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where c.category_name=:category and DATE(p.payment_date) <=:date order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentByCategoryNameAndPaymentDateLessThanEqual(@Param("category") String category, @Param("date") Date date);
    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where c.category_name=:category and DATE(p.payment_date) >=:date order by p.payment_date desc ", nativeQuery = true)
    List<Payment> findPaymentByCategoryNameAndPaymentDateGreaterThanEqual(@Param("category") String category, @Param("date") Date date);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where c.category_name=:category and DATE_FORMAT(p.payment_date, '%d/%m/%Y') = :date order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsByCategoryNameAndExactDate(@Param("category") String category, @Param("date") String date);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo  where c.category_name=:category and (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%)", nativeQuery = true)
    List<Payment> findPaymentsByCategoryNameAndKeyWord(@Param("category") String category, @Param("keyWord") String keyWord);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where c.category_name=:category and f.name=:fieldName and value like %:value%", nativeQuery = true)
    List<Payment> findPaymentsByCategoryNameAndDetail(@Param("category") String category, @Param("fieldName") String name, @Param("value") String value);

    @Query(
            value = "select p.* from category c join sub_category s on c.id=s.category_id  join account a on s.code=a.code join payment p on p.accountNo=a.accountNo join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where c.category_name=:category and value like %:value%", nativeQuery = true)
    List<Payment> findPaymentsByCategoryNameAndDetailValue(@Param("category") String category, @Param("value") String value);


    @Query(
            value = "select p.* from payment p join payment_details pd on p.id=pd.payment_id join fields f on f.id=pd.field_id where (p.customer_name like %:keyWord% or p.msisdn like %:keyWord% or p.payer_name like %:keyWord% or p.externalRefNo like %:keyWord% or p.referenceNo like %:keyWord%) and f.name=:fieldName and value like %:value% order by p.payment_date desc", nativeQuery = true)
    List<Payment> findPaymentsBySearchValuesWithoutDate(@Param("keyWord") String keyword, @Param("fieldName") String name, @Param("value") String value);


}
