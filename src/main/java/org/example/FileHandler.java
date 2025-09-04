package org.example;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHandler {

    private File filesDirectory;
    private File indexHTML;
    private String base="/app";
    private String filesDir ="files";
    private String staticDir ="static";
    private String indexName="index.html";
    
    public FileHandler(){
        filesDirectory=new File(base+File.separator+filesDir);
        if(!filesDirectory.exists()) 
        {filesDirectory.mkdirs();}
        indexHTML=new File(base+File.separator+staticDir+File.separator+indexName);
    }

    private String readFile(File file){
        String fileContents="";
        if(!file.exists()){
            return fileContents;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line="";
            while ((line=reader.readLine())!=null) {
                fileContents+=line+"\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContents;
    }

    public String getIndexHTML(){
        return readFile(indexHTML);
    }
    public String getFile(String fileName ){

        File file=new File(filesDirectory+File.separator+fileName);
        return readFile(file);
    }

    public String getFilesInfo(){

        if(!filesDirectory.exists()) return "";

        String fileInfo="";
        List<File> files=new ArrayList<File>(Arrays.asList(filesDirectory.listFiles()));

        
        for(File file:files){
            fileInfo+=file.getName()+"\n";
        }
        return fileInfo;
    }

    public boolean createFile(String fileName, String content) {
    File file = new File(filesDirectory + File.separator + fileName);
    if(file.exists()) return false;
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        writer.write(content);
        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean deleteFile(String fileName) {
    File file = new File(filesDirectory + File.separator + fileName);
    if (file.exists()) {
        return file.delete();
    }
    return false;
}

}
