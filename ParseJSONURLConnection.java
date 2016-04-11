//this example shows how the parse method would be implemented using a URLConnection object instead of a File object. This assumes that the json file is 
//stored inside the web folder (at the same level of index.jsp file) in your web project.
 public void parse() throws JSONException, IOException {
      
       //File f = new File("C:\\Users\\Lenovo\\Desktop\\users.json");
                  //String jsonString = readFile(f.getPath());
      
        URL url = new URL("http://localhost:8080/Assignment/users.json");               
        URLConnection con = url.openConnection();       
                   
        con.setRequestProperty("Accept-Charset", "UTF-8");
        InputStream response = con.getInputStream();
        BufferedReader reader= new BufferedReader(new InputStreamReader(response));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        String jsonString = stringBuilder.toString();
        
        
                 
        
                  jsonOut = new JSONTokener(jsonString);
                  
                  //continue with your json parsing code
