package com.utility.payments.service;

import com.utility.payments.model.Account;
import com.utility.payments.model.Payment;
import com.utility.payments.model.SubCategory;
import com.utility.payments.repository.PaymentRepository;
import com.utility.payments.repository.SubCategoryRepository;
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

@Path("/subcategories")
public class SubCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(SubCategoryService.class);

    @Autowired
    private SubCategoryRepository subCategoryRepository;
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
    public Response getSubCategories() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return Response.ok(subCategories).build();
    }

    @GET
    @Path("/{name :.+}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getSubCategoryByName(@PathParam("name") String name) {

        SubCategory subCategory = subCategoryRepository.findByName(name);
        return Response.ok(subCategory).build();
    }


    @GET
    @Path("{name: .+}/accounts")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getSubCategories(@PathParam("name") String name) {

        SubCategory subCategory = subCategoryRepository.findByName(name);
        List<Account> accounts = subCategory.getAccounts();
        CacheControl cc = new CacheControl();
        cc.setMaxAge(86400);
        EntityTag etag = new EntityTag(Integer.toString(accounts.hashCode()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);

        if (builder == null) {
            builder = Response.ok(accounts);
            builder.tag(etag);
        }
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("{name: .+}/payments")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getSubCategoriesPayments(@PathParam("name") String name, @Context UriInfo info) {

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

                payments = paymentRepository.findPaymentsBySearchValuesAndSubCategoryName(name, keyword, fieldName, value, startDate, endDate);
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

                payments = paymentRepository.findPaymentsBySearchValuesWithExactDateAndSubCategoryName(name, keyword, fieldName, value, date);
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

                payments = paymentRepository.findPaymentsBySearchValuesAndSubCategoryName(name, keyword, "", value, startDate, endDate);
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

                payments = paymentRepository.findPaymentsByKeywordAndDateValuesAndSubCategoryName(name, keyword, startDate, endDate);
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

                payments = paymentRepository.findPaymentsBySearchValuesWithoutDateAndSubCategoryName(name, keyword, fieldName, value);
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

                payments = paymentRepository.findPaymentsByKeywordAndExactDateValuesAndSubCategoryName(name, keyword, date);
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
                    payments = paymentRepository.findPaymentBySubCategoryNameAndPaymentDateBetweenOrderByPaymentDateDesc(name, format.parse(startDate), format.parse(endDate));
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
                    payments = paymentRepository.findPaymentBySubCategoryNameAndPaymentDateGreaterThanEqual(name, format.parse(startDate));
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
                    payments = paymentRepository.findPaymentBySubCategoryNameAndPaymentDateLessThanEqual(name, format.parse(endDate));
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

                payments = paymentRepository.findPaymentsBySubCategoryNameAndExactDate(name, date);

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
                payments = paymentRepository.findPaymentsBySubCategoryNameAndKeyWord(name, keyword);
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
                payments = paymentRepository.findPaymentsBySubCategoryNameAndDetail(name, fieldName, value);
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
                payments = paymentRepository.findPaymentsBySubCategoryNameAndDetailValue(name, value);
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
            payments = paymentRepository.findPaymentsBySubCategory(name);

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
