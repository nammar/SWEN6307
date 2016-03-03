/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.birzeit.cs.parsers;

/**
 *
 * @author nammar
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
//import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TestParsingWithJSON {

    private static JSONTokener jsonOut;

    public static void main(String myHelpers[]) throws JSONException, IOException {
                       
                File f = new File("/Users/narimanammar/NetBeansProjects/WebApplication1/src/java/edu/birzeit/cs/parsers/file1.json");

                  
                    String jsonString = readFile(f.getPath());
                                
                    jsonOut = new JSONTokener(jsonString);
                    JSONObject output = new JSONObject(jsonOut);
                    
                    int nbThreads = output.getJSONObject("set-up").getInt("nbThreads");
                    
                    JSONObject docs = output.getJSONObject("results");
                    JSONObject output3 = docs.getJSONObject("mdc.xacml.concurrency.PDPThread");
                    JSONObject docs2 = output3.getJSONObject("pdp-evaluate");
                    double rate = docs2.getJSONObject("rate").getDouble("mean");
                    double time = docs2.getJSONObject("duration").getDouble("mean");

                    double fetch = docs.getJSONObject("mdc.xacml.concurrency.pdp.LocalAttributeFinderModule").
                            getJSONObject("fetchAttribute").getJSONObject("duration").getDouble("sum");
                         
            
        
            }
    

    private static String readFile(String file) throws IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }

}
