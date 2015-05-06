/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic.bush;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author omacejovsky
 */
public class Sentence {
    private Integer tail; //index konce vety 
    private Integer head; // index zacatku vety
    private String[] sentence; //veta ulozena v poli po slovech
    private final int[] idSentence; //k sentence, pole kazdemu slovu v poli sentence prideluje jeho cislo v ff.text.xml
    
    public Sentence(Integer tail, Integer head, int[] idSentence, String[] sentence)
    {
        this.tail = tail;
        this.head = head;
        this.sentence = sentence;
        this.idSentence = idSentence;
    }

    /**
     * @return the tail
     */
    public Integer getTail() {
        return tail;
    }

    /**
     * @param tail the tail to set
     */
    public void setTail(Integer tail) {
        this.tail = tail;
    }

    /**
     * @return the head
     */
    public Integer getHead() {
        return head;
    }

    /**
     * @param head the head to set
     */
    public void setHead(Integer head) {
        this.head = head;
    }

    /**
     * @return the sentence
     */
    public String[] getSentence() {
        return sentence;
    }

    /**
     * @param sentence the sentence to set
     */
    public void setSentence(String[] sentence) {
        this.sentence = sentence;
    }
    
    /**
     * toString return array in one string
     * @return one strinf full of parts of array
     */
    public String toString(){
        String result = "";
        for (int i=0; i < sentence.length; i++){
            result += sentence[i];
            result += " ";
        }
        return result;
    }
    
    /**
     * readSyntax() read syntax
     */
    public void readSyntax(){
        try {
            File file = new File("." + File.separator + "src" + File.separator + "ff.syntax.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            NodeList syntaxes = doc.getElementsByTagName("syntax");
            for (int i=0; i < 15/*syntaxes.getLength()*/; i++){
                Element syntax = (Element)syntaxes.item(i);
                
                //zabyvat se jenom temi co maji status 1
                if (Integer.parseInt(syntax.getAttribute("status")) == 1){
                    Integer indicator = lastIndex(syntax.getAttribute("nite:id")); //hodnosta ff.syntax.x
                    Element child = (Element)syntax.getElementsByTagName("nite:child").item(0);
                    String href = child.getAttribute("href");
                    
                    //parser href
                    Pattern pattern = Pattern.compile("[0-9]+");
                    Matcher matcher;
                    matcher = pattern.matcher(href);
                    int from = 0; //zacatek skupiny
                    int to = 0; //konec skupiny
                    if (matcher.find()) {
                        from = Integer.parseInt(matcher.group(0));
                        if(matcher.find()) to = Integer.parseInt(matcher.group(0));
                        else to = from;
                    } 
                    
                    //for skonci kdyz je from vetsi nez tail, tzn. uz jsme u dalsi vety
                    if(from > this.tail) break;
                    
                    //slouceni skupin do poli k sobe
                    int indexTo = 0;
                    int indexFrom = 0;
                    if(from != to){
                        int j;
                        
                        //nalezeni, kde indexu kde zacina a konci cast skupiny v sentece
                        for(j = 0; j < tail-head; j++){
                            if(idSentence[j] == to) indexTo = j;
                            if(idSentence[j] == from) indexFrom = j;
                        }
                        
                        //ulozeni vsech podcasti do jedne
                        while(indexFrom != indexTo){
                            sentence[indexTo-1] = sentence[indexTo-1] + " " + sentence[indexTo];
                            sentence[indexTo] = null;
                            indexTo--;
                        }           
                    }
                    
                    //kvuli vypisu
                    int j;
                    for(j = 0; j < tail-head; j++){
                            if(idSentence[j] == to) indexTo = j;
                            if(idSentence[j] == from) indexFrom = j;
                    }
                    System.out.println(sentence[indexFrom]); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public static int lastIndex(String str){
        return Integer.parseInt(str.split("\\.")[2]);
    }
}
