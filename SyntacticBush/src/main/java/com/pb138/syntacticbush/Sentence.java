
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pb138.syntacticbush;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.xml.sax.SAXException;

/**
 * Trida pro parsovani xml.
 * @author prazak, jouzova
 */
public class Sentence {

    private Integer tail; /** index konce vety */ 
    private Integer head; /** index zacatku vety */
    private String[] sentence; /** veta ulozena v poli po slovech */
    private int[] idSentence; /** pole kazdemu slovu v poli sentence prideluje jeho cislo v ff.text.xml */
    private int[] notTogetherWord; /** pole pro slova, ktera nejsou vedle sebe */
    
    /**
     * Konstruktor sentence.
     * @param head pozice zacatku vety
     * @param text File k ff.text
     * @param syntax File k ff.syntax
     * @param relation File k ff.syntax-relation
     */
    public Sentence(Integer head, File text, File syntax, File relation) {
        this.head = head;
        this.idSentence = null;
        this.sentence = null;
        this.notTogetherWord = null;
        readText(text);
        readSyntax(syntax);
        readRelation(relation);
    }
    /**
     * Vrati idSentence pole obsahujici relation
     * @return idSentence
     */
    public int[] getIdSentence() {
        return this.idSentence;
    }

    /**
     * Vratit pozici pocatku vety
     * @return the head
     */
    public Integer getHead() {
        return head;
    }

    /**
     * Nastavi pozici pocatku vety
     * @param head the head to set
     */
    public void setHead(Integer head) {
        this.head = head;
    }

    /**
     * Vrati pole skupin vety
     * @return sentence
     */
    public String[] getSentence() {
        return sentence;
    }

    /**
     * Rozparsovani ff.syntax, idSentence napleno nite:id, 
     * sentence naplneno skupinami slov nebo null.
     * @param file File k ff.syntax
     */
    private void readSyntax(File file) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            //pomocne pole pro zapamatovani hodnot ff.syntax.x
            int[] array = new int[this.idSentence.length];
            Arrays.fill(array, -1);
            
            notTogetherWord = new int[this.idSentence.length];
            Arrays.fill(notTogetherWord, -1);

            NodeList syntaxes = doc.getElementsByTagName("syntax");
            int m = 0;
            for (int i = 0; i < syntaxes.getLength(); i++) {
                Element syntax = (Element) syntaxes.item(i);

                if (syntax.hasAttribute("status") && Integer.parseInt(syntax.getAttribute("status")) == 1
                        && !"clause".equals(syntax.getAttribute("tag"))) {
                    Integer indicator = lastIndex(syntax.getAttribute("nite:id")); //hodnosta ff.syntax.x

                    
                    int from;
                    NodeList childs = syntax.getElementsByTagName("nite:child");
                    for (int k = 0; k < childs.getLength(); k++) {
                        Element child = (Element) childs.item(k);
                        String href = child.getAttribute("href");

                        //parser href
                        int[] pom = parser(href);
                        from = pom[0];
                        int to = pom[1];
                        
                        //slouceni skupin do poli k sobe
                        int indexTo = 0;
                        int indexFrom = 0;
                        int ok = 0;
                        if (from != to + 1) {
                            int j;

                            //nalezeni, kde indexu kde zacina a konci cast skupiny v s
                            for (j = 0; j < tail - head; j++) {
                                if (idSentence[j] == to) {
                                    indexTo = j;
                                    ok++;
                                }
                                if (idSentence[j] == from) {
                                    indexFrom = j;
                                    
                                    if (childs.getLength() > 1) {
                                        notTogetherWord[m] = indexFrom;
                                        m++;
                                        if (childs.getLength() == k + 1) {
                                            m++;
                                        }
                                    }
                                }
                            }
                            int cover = 0; //nektere jsou nefunkcni protoze se prekryvaji
                            for(j = indexFrom; j < indexTo; j++) {
                                if(sentence[j] == null) {
                                    cover++;
                                }
                                        
                            }
                                    
                            if(cover == 0) {
                                //ulozeni vsech podcasti do jedne
                                while (indexFrom != indexTo) {
                                    sentence[indexTo - 1] = sentence[indexTo - 1] + " " + sentence[indexTo];
                                    sentence[indexTo] = null;
                                    indexTo--;
                                }
                            }
                        }

                    /*    //kvuli vypisu
                        int j;
                        for (j = 0; j < tail - head; j++) {
                            if (idSentence[j] == to) {
                                indexTo = j;
                            }
                            if (idSentence[j] == from) {
                                indexFrom = j;
                            }
                        }*/

                        //ulozeni id z ff.syntax.x
                        if (ok > 0) {
                            array[indexFrom] = indicator;
                        }
                    }
                    //System.out.println(s[indexFrom]); 
                }
            }

            //zkopirovat pomocneho pole do idSentence, od ted je v idSentence pripraveno na relation
            System.arraycopy(array, 0, idSentence, 0, array.length);
            //System.out.println(Arrays.toString(notTogetherWord));
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Vraci treti cast vyrazu ff.neco.cislo.
     * @param str retezec ve tvaru ff.neco.cislo
     * @return cislo
     */
    public static int lastIndex(String str) {
        return Integer.parseInt(str.split("\\.")[2]);
    }
    
    /**
     * Rozparsovani ff.syntax-relation, idSentence naplneno
     * indexy relation.
     * @param file File k ff.syntax-relation
     */
    private void readRelation(File file) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            //pomocne pole pro zapamatovani hodnot ff.syntax-relation.x
            int[] array = new int[this.idSentence.length];
            Arrays.fill(array, -1);
            NodeList srelations = doc.getElementsByTagName("srelation");
            for (int i = 0; i < srelations.getLength(); i++) {
                Element relation = (Element) srelations.item(i);

                Element pointer = (Element) relation.getElementsByTagName("nite:pointer").item(0);
                String href = pointer.getAttribute("href");

                //parser href
                int[] pom = parser(href);
                int pointerID = pom[0]; //ff.syntax of pointer

                Element child = (Element) relation.getElementsByTagName("nite:child").item(0);
                String href1 = child.getAttribute("href");

                //parser href
                pom = parser(href1);
                int childID = pom[0]; //ff.syntax of child

                //to ukazuje na from
                int indexTo = 0;
                int indexFrom = 0;
                int ok = 0;

                //hledani ukazatele a kam ukazuje v poli
                int j;
                for (j = 0; j < idSentence.length; j++) {
                    if (idSentence[j] == pointerID) {
                        ok++;
                        indexTo = j;
                    }
                    if (idSentence[j] == childID) {
                        indexFrom = j;
                        ok++;
                    }
                }

                if (ok > 0) {
                    array[indexFrom] = indexTo;
                }
            }

            //pole idSentence obsahuje -1 pokud slovo na teto pozici v s nikam neukazuje
            //a hodnotu 0-(delka vety), pokud slovo někam ukazuje
            //tzn. v poli idSentence je uloženo na pozici 0, hodnota 3 znamena ze slovo nebo
            //skupina slov na pozici 0 v s ukazuje na slovo nebo skupinu slov na pozici
            //3 v s, tj. s[3]
            System.arraycopy(array, 0, idSentence, 0, array.length);
            System.out.println(Arrays.toString(sentence));
            System.out.println(Arrays.toString(idSentence));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Funkce na parsovani href.
     *
     * @param href atribut nite
     * @return pole, array[0] první indikator, pole [1] druhy indikator
     */
    public int[] parser(String href) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher;
        matcher = pattern.matcher(href);
        int[] array = new int[2];
        if (matcher.find()) {
            array[0] = Integer.parseInt(matcher.group(0));
            if (matcher.find()) {
                array[1] = Integer.parseInt(matcher.group(0));
            } else {
                array[1] = array[0];
            }
        }
        return array;
    }
    
    /**
     * Rozparsovani ff.text, idSentece obsahuje indexy ff.text.x
     * Sentence obsahuje vetu
     * @param file File k ff.text
     */
    private void readText(File file) {
        List<String> phrases = new ArrayList<>(); //list slov ve vete
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            NodeList sentences = doc.getElementsByTagName("s");
            for (int i = 0; i < sentences.getLength(); i++) {
                Element s = (Element) sentences.item(i);
                if (lastIndex(s.getAttribute("nite:id")) == head) {
                    NodeList tokens = s.getElementsByTagName("token");
                    idSentence = new int[tokens.getLength()];
                    for (int j = 0; j < tokens.getLength(); j++) {
                        Element token = (Element) tokens.item(j);
                        idSentence[j] = lastIndex(token.getAttribute("nite:id"));
                        phrases.add(token.getTextContent());
                    }
                    tail = head + tokens.getLength();
                    sentence = new String[phrases.size()];
                    phrases.toArray(sentence);
                    for (int k = 0; k < sentence.length; k++) {
                        if (sentence[k].equals(".") || sentence[k].equals(",")) {
                            sentence[k-1] += sentence[k];
                            sentence[k] = null;
                        }
                    }
                }

            }
        }
        catch (ParserConfigurationException | SAXException | IOException | DOMException e){
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        String result = "";
        for (String s : sentence) {
            if (s != null) {
                result += "[" + s + "]";
                result += " ";
            }

        }
        return result;
    }

    /**
     * @return the notTogetherWord
     */
    public List<Integer> getNotTogetherWord() {
        List<Integer> result = new ArrayList<>();
        for (int i=0; i < notTogetherWord.length; i++){
            result.add(notTogetherWord[i]);
        }
        return result;
    }

}
