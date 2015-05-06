/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic.bush;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author omacejovsky
 */
public class SyntacticBush {

    /**
     * @param args the command line arguments
     */

    
    
    public static void main(String[] args) {
        int head,tail;
        List<String> phrases = new ArrayList<>();
        try {
            File file = new File("." + File.separator + "src" + File.separator + "ff.text.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());
            NodeList sentences = doc.getElementsByTagName("s");
            Element sentence = (Element)sentences.item(0);
            head = lastIndex(sentence.getAttribute("nite:id"));
            NodeList tokens = sentence.getElementsByTagName("token");
            for (int i=0; i < tokens.getLength(); i++ ){
                Element token = (Element) tokens.item(i);
                phrases.add(Integer.toString(lastIndex(token.getAttribute("nite:id"))) + tokens.item(i).getTextContent());
            }
            tail = head + tokens.getLength();
            Sentence s = new Sentence(tail,head, phrases.toArray(new String[phrases.size()]));
            System.out.println(s);
            //zavolani konstruktoru Sentence(head,tail,phrase)
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }
    
    public static int lastIndex(String str){
        return Integer.parseInt(str.split("\\.")[2]);
    }

}
