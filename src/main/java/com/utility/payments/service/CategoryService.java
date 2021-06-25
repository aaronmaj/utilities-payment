package com.utility.payments.service;


import com.utility.payments.model.Category;
import com.utility.payments.model.Payment;
import com.utility.payments.model.SubCategory;
import com.utility.payments.repository.CategoryRepository;
import com.utility.payments.repository.PaymentRepository;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/categories")
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Context
    private Request request;
    @Context
    MessageContext context;
    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCategorie() {

        List<Category> categoryList = categoryRepository.findAll();
        CacheControl cc = new CacheControl();
        cc.setMaxAge(86400);
        EntityTag etag = new EntityTag(Integer.toString(categoryList.hashCode()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

        if (builder == null) {
            builder = Response.ok(categoryList);
            builder.tag(etag);
        }
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("/{name :.+}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCategoryByName(@PathParam("name") String name) {

        Category category = categoryRepository.findByName(name);
        return Response.ok(category).build();
    }


    @GET
    @Path("{name: .+}/subcategories")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getSubCategories(@PathParam("name") String name) {

        context = new MessageContextImpl(PhaseInterceptorChain.getCurrentMessage());
        request = context.getRequest();

        Category category = categoryRepository.findByName(name);
        List<SubCategory> subCategories = category.getSubCategories();
        CacheControl cc = new CacheControl();
        cc.setMaxAge(86400);
        EntityTag etag = new EntityTag(Integer.toString(subCategories.hashCode()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

        if (builder == null) {
            builder = Response.ok(subCategories);
            builder.tag(etag);
        }
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("{name: .+}/payments")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCategoriesPayments(@PathParam("name") String name, @Context UriInfo info) {

        context = new MessageContextImpl(PhaseInterceptorChain.getCurrentMessage());
        request = context.getRequest();
        List<Payment> payments = new ArrayList<>();
        List<PathSegment> pathSegments = info.getPathSegments();

        Optional<PathSegment> optionalSegment = pathSegments.stream().filter(pathSegment ->
                pathSegment.getMatrixParameters().getFirst("keyword") != null ||
                        pathSegment.getMatrixParameters().getFirst("startdate") != null ||
                        pathSegment.getMatrixParameters().getFirst("enddate") != null ||
                        pathSegment.getMatrixParameters().getFirst("date") != null ||
                        pathSegment.getMatrixParameters().getFirst("fieldname") != null ||
                        pathSegment.getMatrixParameters().getFirst("value") != null)
                .findAny();

        if (optionalSegment.isPresent()) {

            PathSegment segment = optionalSegment.get();
            String keyword = segment.getMatrixParameters().getFirst("keyword");
            String startDate = segment.getMatrixParameters().getFirst("startdate");
            String endDate = segment.getMatrixParameters().getFirst("enddate");
            String date = segment.getMatrixParameters().getFirst("date");
            String fieldName = segment.getMatrixParameters().getFirst("fieldname");
            String value = segment.getMatrixParameters().getFirst("value");
            logger.info("Keyword : {} - Start date : {} - End date: {} - Date: {} -Field name : {} - value : {}", keyword, startDate, endDate, date, fieldName, value);
            if (keyword != null && startDate != null && endDate != null && fieldName != null && value != null) {
                SimpleDateFormat inFormat = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    startDate = format.format(inFormat.parse(startDate));
                    endDate = format.format(inFormat.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                payments = paymentRepository.findPaymentsBySearchValuesAndCategoryName(name, keyword, fieldName, value, startDate, endDate);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (keyword != null && date != null && fieldName != null && value != null) {
                date = date.replace(".", "/");

                payments = paymentRepository.findPaymentsBySearchValuesWithExactDateAndCategoryName(name, keyword, fieldName, value, date);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (keyword != null && startDate != null && endDate != null && value != null) {

                SimpleDateFormat inFormat = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    startDate = format.format(inFormat.parse(startDate));
                    endDate = format.format(inFormat.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                payments = paymentRepository.findPaymentsBySearchValuesAndCategoryName(name, keyword, "", value, startDate, endDate);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (keyword != null && startDate != null && endDate != null) {
                SimpleDateFormat inFormat = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    startDate = format.format(inFormat.parse(startDate));
                    endDate = format.format(inFormat.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                payments = paymentRepository.findPaymentsByKeywordAndDateValuesAndCategoryName(name, keyword, startDate, endDate);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (keyword != null && fieldName != null && value != null) {

                payments = paymentRepository.findPaymentsBySearchValuesWithoutDateAndCategoryName(name, keyword, fieldName, value);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (keyword != null && date != null) {
                date = date.replace(".", "/");

                payments = paymentRepository.findPaymentsByKeywordAndExactDateValuesAndCategoryName(name, keyword, date);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (startDate != null && endDate != null) {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                try {
                    payments = paymentRepository.findPaymentByCategoryNameAndPaymentDateBetweenOrderByPaymentDateDesc(name, format.parse(startDate), format.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (startDate != null) {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                try {
                    payments = paymentRepository.findPaymentByCategoryNameAndPaymentDateGreaterThanEqual(name, format.parse(startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (endDate != null) {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                try {
                    payments = paymentRepository.findPaymentByCategoryNameAndPaymentDateLessThanEqual(name, format.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (date != null) {
                date = date.replace(".", "/");

                payments = paymentRepository.findPaymentsByCategoryNameAndExactDate(name, date);

                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (keyword != null) {
                payments = paymentRepository.findPaymentsByCategoryNameAndKeyWord(name, keyword);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (fieldName != null && value != null) {
                payments = paymentRepository.findPaymentsByCategoryNameAndDetail(name, fieldName, value);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            } else if (value != null) {
                payments = paymentRepository.findPaymentsByCategoryNameAndDetailValue(name, value);
                CacheControl cc = new CacheControl();
                cc.setMaxAge(86400);
                EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
                Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

                if (builder == null) {
                    builder = Response.ok(payments);
                    builder.tag(etag);
                }
                builder.cacheControl(cc);
                return builder.build();
            }
        } else {
            payments = paymentRepository.findPaymentsByCategory(name);
            CacheControl cc = new CacheControl();
            cc.setMaxAge(86400);
            EntityTag etag = new EntityTag(Integer.toString(payments.hashCode()));
            Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

            if (builder == null) {
                builder = Response.ok(payments);
                builder.tag(etag);
            }
            builder.cacheControl(cc);
            return builder.build();

        }
        return Response.ok(payments).build();
    }
}
