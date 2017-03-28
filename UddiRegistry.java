package edu.wayne.registry;

import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.Properties;
import java.util.Vector;
import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.binding.AccessPoint;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.binding.TModelInstanceDetails;
import org.uddi4j.datatype.binding.TModelInstanceInfo;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.datatype.service.BusinessServices;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.BindingDetail;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.BusinessInfo;
import org.uddi4j.response.BusinessList;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.Result;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.response.ServiceInfo;
import org.uddi4j.response.ServiceInfos;
import org.uddi4j.response.ServiceList;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.response.TModelInfo;
import org.uddi4j.response.TModelList;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.util.KeyedReference;
import org.uddi4j.util.TModelBag;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UddiRegistry {

    Connection connection = null;

    public UddiRegistry() {
    }

    public static void main(String[] args) {
        String publishURL = "http://localhost:8080/juddi/publish";
        String queryURL = "http://localhost:8080/juddi/inquiry";
        // Provide your user name and password
        String username = "jdoe";
        String password = "password";

        UddiRegistry po = new UddiRegistry();
        po.makeConnection(publishURL, queryURL);

        Vector servicenames = po.findBusiness(publishURL, queryURL, "CSC 5991", "News");
        String ng = po.getNotificationGraph(queryURL, publishURL, username, password, "WS5", "CSC 5991", "Notification Graph");
        System.out.println("Hi from Nana: " + servicenames.get(0));
        System.out.println("Hi from Nariman: " + ng);
    }

    /**
     * Establishes a connection to a registry.
     * @param publishUrl   the URL of the publish registry
     * @param queryUrl   the URL of the query registry
     */
    public void makeConnection(String publishUrl, String queryUrl) {
        /*
         * Define connection configuration properties.
         * To publish, you need both the query URL and the
         * publish URL.
         */
        Properties props = new Properties();
        props.setProperty("javax.xml.registry.lifeCycleManagerURL", publishUrl);
        props.setProperty("javax.xml.registry.queryManagerURL", queryUrl);
        props.setProperty("javax.xml.registry.factoryClass", "com.sun.xml.registry.uddi.ConnectionFactoryImpl");

        try {
            // Create the connection, passing it the
            // configuration properties
            ConnectionFactory factory = ConnectionFactory.newInstance();
            factory.setProperties(props);
            connection = factory.createConnection();
            System.out.println("Created connection to registry");
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.close();
                } catch (JAXRException je) {
                }
            }
        }
    }



    public Vector findBusiness(String publishURL, String queryURL, String businessName, String topic) {

        // Construct a UDDIProxy object.
        UDDIProxy proxy = new UDDIProxy();
        Vector businessInfoVector = null;
        Vector serviceInfoVector = null;
        Vector servicenames = new Vector();

        Vector services = new Vector(  );

        //creating vector of Name Object
        Vector names = new Vector();
        names.add(new Name(businessName));

        // Setting FindQualifiers to 'caseSensitiveMatch'
        FindQualifiers findQualifiers = new FindQualifiers();
        Vector qualifier = new Vector();
        qualifier.add(new FindQualifier("caseSensitiveMatch"));
        findQualifiers.setFindQualifierVector(qualifier);

        try {
            // Select the desired UDDI server node
            proxy.setInquiryURL(queryURL);
            proxy.setPublishURL(publishURL);


            BusinessList businessList = proxy.find_business(names, null, null, null, null, findQualifiers, 5);
            businessInfoVector = businessList.getBusinessInfos().getBusinessInfoVector();

            for (int i = 0; i < businessInfoVector.size(); i++) {
                BusinessInfo businessInfo = (BusinessInfo) businessInfoVector.elementAt(i);

                CategoryBag c = new CategoryBag();
                KeyedReference params1 = new KeyedReference("category", topic);
                params1.setTModelKey("uddi:uddi-org:general_keywords");
                c.add(params1);


                ServiceList serviceList = proxy.find_service(businessInfo.getBusinessKey(), servicenames, c, null, findQualifiers, 5);
                serviceInfoVector = serviceList.getServiceInfos().getServiceInfoVector();

                for (int j = 0; j < serviceInfoVector.size(); j++) {
                    ServiceInfo serviceInfo = (ServiceInfo) serviceInfoVector.elementAt(j);
                    // Print name for each service
                    System.out.println("\nName of Service : "
                            + serviceInfo.getDefaultNameString());
                    services.add(serviceInfo.getDefaultNameString());
                    System.out.println("Service key     : "
                            + serviceInfo.getServiceKey());
                    ServiceDetail serviceDet = proxy.get_serviceDetail(serviceInfo.getServiceKey());
                    BusinessService service = (BusinessService) serviceDet.getBusinessServiceVector().get(0);
                    try {
                        CategoryBag bag = service.getCategoryBag();
                        Vector paramBag = bag.getKeyedReferenceVector();
                        for (Iterator iter = paramBag.iterator(); iter.hasNext();) {
                            KeyedReference element = (KeyedReference) iter.next();
                            System.out.println("parameter identifier : " + element.getKeyName() + " value:" + element.getKeyValue());
                        }
                    } catch (NullPointerException e) {
                        System.out.println("no Categorybag");
                    }
                }
        }

        return services;
    }  catch(UDDIException  e) {
            DispositionReport dr = e.getDispositionReport();
            if (dr != null) {
            System.out.println("UDDIException faultCode:" + e.getFaultCode()
                    + "\n operator:" + dr.getOperator()
                    + "\n generic:" + dr.getGeneric());

            Vector results = dr.getResultVector();
            for (int i = 0; i < results.size(); i++) {
                Result r = (Result) results.elementAt(i);
                System.out.println("\n errno:" + r.getErrno());
                if (r.getErrInfo() != null) {
                    System.out.println("\n errCode:" + r.getErrInfo().getErrCode()
                            + "\n errInfoText:" + r.getErrInfo().getText());
                }
            }
        }
        e.printStackTrace();
    } // Catch any other exception that may occur
    catch(Exception e) {
        e.printStackTrace();
        }
return services;
    }


    public String getNotificationGraph(String queryURL, String publishURL, String username, String password,
            String serviceName, String businessName, String tModelName) {

        String url = null;
        UDDIProxy proxy = new UDDIProxy();
        Vector businessInfoVector = null;
        Vector serviceInfoVector = null;
        String serviceKey = null;
        String tModelKey = null;

        try {
            // Select the desired UDDI server node
            proxy.setInquiryURL(queryURL);
            proxy.setPublishURL(publishURL);

            // Get an authorization token
            System.out.println("\nGet authtoken");

            // Pass in userid and password registered at the UDDI site
            AuthToken token = proxy.get_authToken(username, password);
            System.out.println("Returned authToken:" + token.getAuthInfoString());

            Vector snames = new Vector();
            snames.add(new Name(serviceName));
            Vector names = new Vector();
            names.add(new Name(businessName));

            FindQualifiers findQualifiers = new FindQualifiers();
            Vector qualifier = new Vector();
            qualifier.add(new FindQualifier("caseSensitiveMatch"));
            findQualifiers.setFindQualifierVector(qualifier);
            BusinessList businessList = proxy.find_business(names, null, null, null, null, findQualifiers, 5);
            businessInfoVector = businessList.getBusinessInfos().getBusinessInfoVector();

            for (int i = 0; i< businessInfoVector.size(); i++) {
                BusinessInfo businessInfo = (BusinessInfo) businessInfoVector.elementAt(i);
                ServiceList serviceList = proxy.find_service(businessInfo.getBusinessKey(), snames, null, null, findQualifiers, 5);
                serviceInfoVector = serviceList.getServiceInfos().getServiceInfoVector();

                for (int j = 0; j< serviceInfoVector.size(); j++) {
                    ServiceInfo serviceInfo = (ServiceInfo) serviceInfoVector.elementAt(j);
                    serviceKey = serviceInfo.getServiceKey();
                    System.out.println("Found:The Service Name: " + serviceInfo.getDefaultNameString());
                    System.out.println("Found:The Service Key: " + serviceKey);
                }
            }

            //Get the tModel key from the name
            TModelList tModelList = proxy.find_tModel(tModelName + serviceName, null, null, findQualifiers, 5);
            Vector tModelInfoVector = tModelList.getTModelInfos().getTModelInfoVector();

            // Try to delete any tModel by this name. Multiple tModels
            // with the same name may have been created with different
            // UDDI userids. Deletes will fail for tModels not created
            // by this UDDI userid.


            for (int i = 0; i
                    < tModelInfoVector.size(); i++) {
                TModelInfo tModelInfo = (TModelInfo) tModelInfoVector.elementAt(i);

                // Print name for each business
                System.out.println("Found:The TModel Name: " + tModelInfo.getNameString());
                System.out.println("The TModel Key : " + tModelInfo.getTModelKey());
                tModelKey = tModelInfo.getTModelKey();


            }



            ///////////////////////////////////
            // Creating the TModel Bag
            TModelBag tModelBag = new TModelBag();
            Vector tModelKeyVector = new Vector();
            tModelKeyVector.add(tModelKey);
            tModelBag.setTModelKeyStrings(tModelKeyVector);
            //////////////////



            // **** Find the Binding Template .
            // And setting the maximum rows to be returned as 5.
            BindingDetail bindingDetailReturned = proxy.find_binding(findQualifiers, serviceKey, tModelBag, 5);

            // Process returned BindingDetail object
            Vector bindingTemplatesFound = bindingDetailReturned.getBindingTemplateVector();



            for (int j = 0; j
                    < bindingTemplatesFound.size(); j++) {

                BindingTemplate bindingTemplateFound = (BindingTemplate) (bindingTemplatesFound.elementAt(j));
                System.out.println("BindingKey Found: " + bindingTemplateFound.getBindingKey());

                url = bindingTemplateFound.getAccessPoint().getText();


            } //    i.getInstanceDetails().get
            return url;



        } // Handle possible errors
        catch (UDDIException e) {
            DispositionReport dr = e.getDispositionReport();


            if (dr != null) {
                System.out.println("UDDIException faultCode:" + e.getFaultCode()
                        + "\n operator:" + dr.getOperator()
                        + "\n generic:" + dr.getGeneric());

                Vector results = dr.getResultVector();


                for (int i = 0; i
                        < results.size(); i++) {
                    Result r = (Result) results.elementAt(i);
                    System.out.println("\n errno:" + r.getErrno());


                    if (r.getErrInfo() != null) {
                        System.out.println("\n errCode:" + r.getErrInfo().getErrCode()
                                + "\n errInfoText:" + r.getErrInfo().getText());


                    }
                }
            }
            e.printStackTrace();



        } // Catch any other exception that may occur
        catch (Exception e) {
            e.printStackTrace();



        }
        return url;

    }
}


