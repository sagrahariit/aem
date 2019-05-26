package com.shailesh.agrahari.core.download.assets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.ServerException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Iterator;
import javax.servlet.ServletOutputStream ;
 
 
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.jcr.Session;
 
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceResolver; 
import org.apache.sling.api.resource.Resource; 
//QueryBuilder APIs
import com.day.cq.search.QueryBuilder; 
import com.day.cq.search.Query; 
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.result.SearchResult;
import com.day.cq.search.result.Hit; 
  
//DAM API
import com.day.cq.dam.api.Asset ; 
 
 
 
/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.resourceTypes="+ "utilities/components/structure/page",
                "sling.servlet.selectors=" + "assets"
        })
public class DownloadAssets  extends SlingAllMethodsServlet{
     
    //Set up References
    /** Default log. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
                
    private Session session;
                    
    //Inject a Sling ResourceResolverFactory
    @Reference
    private ResourceResolverFactory resolverFactory;
                
    @Reference
    private QueryBuilder builder;
         
        @Override
        protected void doGet(final SlingHttpServletRequest req,
                final SlingHttpServletResponse resp) throws ServletException, IOException {
             
                        
            Map<String, Object> param = new HashMap<String, Object>();
            param.put(ResourceResolverFactory.SUBSERVICE, "datawrite");
            ResourceResolver resolver = null;
 
            try {
            	log.info("In doGet - Shailesh");
                        
                //Invoke the adaptTo method to create a Session used to create a QueryManager
             resolver = resolverFactory.getServiceResourceResolver(param);
                session = resolver.adaptTo(Session.class);
 
                  
               // create query description as hash map (simplest way, same as form post)
               Map<String, String> map = new HashMap<String, String>();
              
               //set QueryBuilder search criteria                   
               map.put("type", "dam:Asset");
               map.put("path", "/content/dam/we-retail"); 
              
                
               builder= resolver.adaptTo(QueryBuilder.class);
                 
               //INvoke the Search query
               Query query = builder.createQuery(PredicateGroup.create(map), session);
                 
               SearchResult sr= query.getResult();
                 
               //write out to the AEM Log file
               log.info("Search Results: " +sr.getTotalMatches() ) ;
                 
               //Create a MAP to store results
               Map<String, InputStream> dataMap = new HashMap<String, InputStream>();
             
               // iterating over the results
               for (Hit hit : sr.getHits()) {
                     
                   //Convert the HIT to an asset - each asset will be placed into a ZIP for downloading
                   String path = hit.getPath();
                   Resource rs = resolver.getResource(path);
                   Asset asset = rs.adaptTo(Asset.class);   
                       
                   //We have the File Name and the inputstream
                   InputStream data = asset.getOriginal().getStream();
                   String name =asset.getName(); 
                                 
                  //Add to map
                   dataMap.put(name, data); // key is fileName and value is inputStream - this will all be placed in ZIP file
              }
                            
               //ZIP up the AEM DAM Assets
               byte[] zip = zipFiles(dataMap);
                 
               //
               // Sends the response back to the user / browser. The
               // content for zip file type is "application/zip". We
               // also set the content disposition as attachment for
               // the browser to show a dialog that will let user 
               // choose what action will he do to the sent content.
               //
                 
               ServletOutputStream sos = resp.getOutputStream();
                 
               resp.setContentType("application/zip");
               resp.setHeader("Content-Disposition", "attachment;filename=dam.zip");
                 
                 
               // Write bytes to tmp file.
               sos.write(zip);
               sos.flush();    
               log.info("The ZIP is sent" ) ;    
            }
            catch(Exception e)
            {
                log.info("OH NO-- AN EXCEPTION: " +e.getMessage() );
            }
          }
             
             
             
           /**
            * Create the ZIP with AEM DAM Assets.
            */
           private byte[] zipFiles(Map data) throws IOException {
                
                 
                 
                 
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               ZipOutputStream zos = new ZipOutputStream(baos);
               byte bytes[] = new byte[2048];
               Iterator<Map.Entry<String, InputStream>> entries = data.entrySet().iterator();
                 
               while (entries.hasNext()) {
                   Map.Entry<String, InputStream> entry = entries.next();
                     
                   String fileName =(String) entry.getKey(); 
                   InputStream is1 =(InputStream) entry.getValue(); 
                     
                   BufferedInputStream bis = new BufferedInputStream(is1);
         
                   //populate the next entry of the ZIP with the AEM DAM asset
                   zos.putNextEntry(new ZipEntry(fileName));
         
                   int bytesRead;
                   while ((bytesRead = bis.read(bytes)) != -1) {
                       zos.write(bytes, 0, bytesRead);
                        
                   }
                   zos.closeEntry();
                   bis.close();
                   is1.close();
                     
                     
               }
                 
              zos.flush();
               baos.flush();
               zos.close();
               baos.close();
         
               return baos.toByteArray();
           }
         
       }
