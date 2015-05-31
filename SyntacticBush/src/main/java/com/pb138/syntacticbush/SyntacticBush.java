/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pb138.syntacticbush;

//import static com.pb138.syntacticbush.Sentence;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for connect of class
 * @author prazak,jouzova, priadka, macejovsky
 */
public class SyntacticBush {
    /**
     * @param args the command line arguments
     */
    private int[] allSentencesIndexes;
    
    /**
     * Constructor
     * @param file
     */
    public SyntacticBush(File file) {
        loadAllSentences(file);
    }
    
    /**
     * Method for load all sentence
     * @param file 
     */
    private void loadAllSentences(File file) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            NodeList sentences = doc.getElementsByTagName("s");
            allSentencesIndexes = new int[sentences.getLength()];
            for (int i = 0; i < sentences.getLength(); i++) {
                Element sentence = (Element) sentences.item(i);
                allSentencesIndexes[i] = lastIndex(sentence.getAttribute("nite:id"));

            }

        } catch (ParserConfigurationException | DOMException e) {
            e.printStackTrace();
        } catch (SAXException | IOException ex) {
            Logger.getLogger(SyntacticBush.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Main
     * @param args 
     */
    public static void main(String[] args) {
        String pack = "01"; 
        File resources = new File("." + File.separator + "data" + File.separator + pack);
        File[] files = resources.listFiles();        
        List<Sentence> eval = new ArrayList<>();
        SyntacticBush bush = null;
        for (File f: files) {
            String path = f.getAbsolutePath();
            File text = new File(path + File.separator +"ff.text.xml");
            File syntax = new File(path + File.separator +  "ff.syntax.xml");
            File relation = new File(path + File.separator + "ff.syntax-relation.xml");
            bush = new SyntacticBush(text);
            Sentence s = new Sentence(bush.allSentencesIndexes[10], text, syntax, relation);
            eval.add(s);
            
        }
        
        Evaluation e = new Evaluation(eval);
        
        
        System.out.println(e.getProbabilitySentence().toString());
        System.out.println(bush.documentToString(e.createDocument()));
    }

    /**
     * Function for split str and return its third part converted to int
     *
     * @param str string to split
     * @return third part of ff.text.x
     */
    public static int lastIndex(String str) {
        return Integer.parseInt(str.split("\\.")[2]);
    } 
    /**
     * @return the allSentencesIndexes
     */
    public int[] getAllSentencesIndexes() {
        return allSentencesIndexes;
    }
    
    /**
     * Method for convert document to String
     * @param doc document
     */
    public static String documentToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (IllegalArgumentException | TransformerException ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

}
