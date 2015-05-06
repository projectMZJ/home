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
        try {
            File file = new File(".\\src\\ff.text.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());
            NodeList sentences = doc.getElementsByTagName("s");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
