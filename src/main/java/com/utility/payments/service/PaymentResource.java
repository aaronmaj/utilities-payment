package com.utility.payments.service;


import com.utility.payments.model.Category;
import com.utility.payments.model.Detail;
import com.utility.payments.model.Payment;
import com.utility.payments.repository.CategoryRepository;
import com.utility.payments.repository.DetailRepository;
import com.utility.payments.repository.FieldRepository;
import com.utility.payments.repository.PaymentRepository;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Path("/payments")
public class PaymentResource {

    private static final Logger logger = LoggerFactory.getLogger(PaymentResource.class);
    @Context
    MessageContext context;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private DetailRepository detailRepository;
    @Autowired
    private FieldRepository fieldRepository;
    @Context
    private HttpServletRequest servletRequest;
    @Autowired
    private CategoryRepository categoryRepository;
    @Context
    private Request request;

    @POST
    @Path("/")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response recordPayment(Payment payment) {
        logger.info("Persisting payment {} ", payment.getReferenceNo());
        payment.getDetails().stream().forEach(detail -> detail.setPaymentId(paymentRepository.getNextId()));
        Payment recorded = paymentRepository.save(payment);
        logger.info("Payment id : {}", recorded.getId());
        return Response.created(URI.create("/payments/" + recorded.getId())).build();
    }

    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPayments(@Context UriInfo info) {

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

                payments = paymentRepository.findPaymentsBySearchValues(keyword, fieldName, value, startDate, endDate);
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

                payments = paymentRepository.findPaymentsBySearchValuesWithExactDate(keyword, fieldName, value, date);
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
                payments = paymentRepository.findPaymentsBySearchValues(keyword, "", value, startDate, endDate);
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
                payments = paymentRepository.findPaymentsByKeywordAndDateValues(keyword, startDate, endDate);
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
                payments = paymentRepository.findPaymentsBySearchValuesWithoutDate(keyword, fieldName, value);
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
                payments = paymentRepository.findPaymentsByKeywordAndExactDateValues(keyword, date);
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
                    payments = paymentRepository.findPaymentByPaymentDateBetweenOrderByPaymentDateDesc(format.parse(startDate), format.parse(endDate));
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
                    payments = paymentRepository.findPaymentByPaymentDateGreaterThanEqual(format.parse(startDate));
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
                    payments = paymentRepository.findPaymentByPaymentDateLessThanEqual(format.parse(endDate));
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

                payments = paymentRepository.findPaymentsByExactDate(date);

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
                payments = paymentRepository.findPaymentsByKeyWord(keyword);
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
                payments = paymentRepository.findPaymentsByDetail(fieldName, value);
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
                payments = paymentRepository.findPaymentsByDetailValue(value);
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
        }
        else {
            logger.info("Get list of all payments available .....");
            payments = paymentRepository.findAllPayments();
            logger.info("Payments successfully retrieved .....");
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

    /*
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPaymentsByRange(@QueryParam("offset") int offset, @QueryParam("offset")int limit) {
        List<Payment> payments = paymentRepository.findAll(new PageRequest(offset, limit, Sort.Direction.DESC, "id")).getContent();
        logger.info("Page size : {}",payments.size());
        return Response.ok(payments).build();
    }*/
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPaymentById(@PathParam("id") Long id) {
        Payment payment = paymentRepository.getById(id);
        return Response.ok(payment).build();
    }

    @GET
    @Path("/{id}/details")
    @Produces("application/json")
    public Response getPaymentDetails(@PathParam("id") Long id) {
        List<Detail> details = detailRepository.findByPaymentId(id);
        //List<Detail> details = paymentRepository.findDistinctProjectionsById(id).get(0).getDetails();
        return Response.ok(details).build();
    }

    @GET
    @Path("/categories")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPaymentCategories() {
        List<Category> categories = categoryRepository.findAll();
        return Response.ok(categories).build();
    }
}
