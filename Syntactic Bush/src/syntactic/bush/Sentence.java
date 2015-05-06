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

/**
 *
 * @author omacejovsky
 */
public class Sentence {
    private Integer tail;   
    private Integer head;
    private String[] sentence;
    
    public Sentence(Integer tail, Integer head, String[] sentence)
    {
        this.tail = tail;
        this.head = head;
        this.sentence = sentence;
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
    
    public void readSyntax(){
        try {
            File file = new File("." + File.separator + "src" + File.separator + "ff.syntax.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());
            NodeList syntaxes = doc.getElementsByTagName("syntax nite:");
            for (int i=0; i < syntaxes.getLength(); i++){
                Element syntax = (Element)syntaxes.item(i);
                if (Integer.parseInt(syntax.getAttribute("status")) == 1){
                    Element child = (Element)syntax.getElementsByTagName("nite:child").item(0);
                    String subString = "ff.text.[0-9]*"; //substring of the atribute href
                    int from,to;
                    String href = child.getAttribute("href");
                    int fromIndex = href.indexOf(subString); //indexOf(String str) returns first index of the string when str occurs
                    int toIndex = href.lastIndexOf(subString); //lastIndexOf returns last index of the string when str occurs
                    
                }
                
            }
            
            NodeList tokens = sentence.getElementsByTagName("token");
            for (int i=0; i < tokens.getLength(); i++ ){
                System.out.println(tokens.item(i).getTextContent());
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
   
    
}
