package com.utility.payments.providers;

import com.utility.payments.obr.model.Payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Aaron MAJAMBO on 28/02/2018.
 */

@Provider
@Produces({"application/xml", "application/*+xml", "text/xml"})
@Consumes({"application/xml", "application/*+xml", "text/xml"})
public class MessageTransformer implements MessageBodyReader<Payment>,MessageBodyWriter<Payment> {
    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Payment readFrom(Class<Payment> type, Type type1, Annotation[] antns, MediaType mt, MultivaluedMap<String, String> mm, InputStream in) throws IOException, WebApplicationException {

        JAXBContext context = null;
        Unmarshaller toJavaObj = null;
        Payment javaDomainObject = null;


        try {
            context = JAXBContext.newInstance(Payment.class);
            toJavaObj = context.createUnmarshaller();
            javaDomainObject = (Payment) toJavaObj.unmarshal(in);

        } catch (JAXBException ex) {
            Logger.getLogger(MessageTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return javaDomainObject;
    }

    @Context
    protected Providers providers;

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Payment o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Payment o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        try {
            ContextResolver<JAXBContext> resolver
                    = providers.getContextResolver(JAXBContext.class, mediaType);
            JAXBContext jaxbContext;
            if(null == resolver || null == (jaxbContext = resolver.getContext(aClass))) {
                jaxbContext = JAXBContext.newInstance(aClass);
            }
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(o, outputStream);
        } catch(JAXBException jaxbException) {
            throw new WebApplicationException(jaxbException);
        }
    }
}
