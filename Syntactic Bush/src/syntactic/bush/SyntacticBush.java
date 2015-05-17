/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic.bush;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author omacejovsky
 */
public class SyntacticBush {

    /**
     * @param args the command line arguments
     */   
    public static void main(String[] args) {
        int head,tail; //zacatek a konec vety
        List<String> phrases = new ArrayList<>(); //list slov ve vete
        int[] idSentence; //pole id z ff.text
        try {
            File file = new File("." + File.separator + "src" + File.separator + "ff.text.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            NodeList sentences = doc.getElementsByTagName("s");
            Element sentence = (Element)sentences.item(0);
            head = lastIndex(sentence.getAttribute("nite:id"));
            NodeList tokens = sentence.getElementsByTagName("token");
            idSentence = new int[tokens.getLength()];
            for (int i=0; i < tokens.getLength(); i++ ){
                Element token = (Element) tokens.item(i);
                idSentence[i] = lastIndex(token.getAttribute("nite:id"));
                phrases.add(/*Integer.toString(lastIndex(token.getAttribute("nite:id"))) +*/ tokens.item(i).getTextContent());
            }
            tail = head + tokens.getLength();
            Sentence s = new Sentence(tail,head,idSentence,phrases.toArray(new String[phrases.size()]));
            System.out.println(s);
            s.readSyntax();
            s.readRelation();
            s.createDocument();
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Function for split str and return its third part converted to int
     * @param str string to split
     * @return third part of ff.text.x
     */
    public static int lastIndex(String str){
        return Integer.parseInt(str.split("\\.")[2]);
    }
}