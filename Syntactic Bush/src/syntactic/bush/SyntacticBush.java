/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic.bush;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.commons.io.*;

/**
 *
 * @author omacejovsky
 */
public class SyntacticBush {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File file = new File("." + File.separator + "src" + File.separator + "ff.text.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());
            NodeList sentences = doc.getElementsByTagName("s");
            Element sentence = (Element)sentences.item(0);
            NodeList tokens = sentence.getElementsByTagName("token");
            for (int i=0; i < tokens.getLength(); i++ ){
                System.out.println(tokens.item(i).getTextContent());
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
