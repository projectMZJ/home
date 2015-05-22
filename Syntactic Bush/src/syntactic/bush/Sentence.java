
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic.bush;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;

/**
 *
 * @author omacejovsky
 */
public class Sentence {

    private Integer tail; //index konce vety 
    private Integer head; // index zacatku vety
    private String[] sentence; //veta ulozena v poli po slovech
    private final int[] idSentence; //k sentence, pole kazdemu slovu v poli sentence prideluje jeho cislo v ff.text.xml

    public Sentence(Integer tail, Integer head, int[] idSentence, String[] sentence) {
        this.tail = tail;
        this.head = head;
        this.sentence = sentence;
        this.sentence[this.sentence.length-2] = this.sentence[this.sentence.length-2] + this.sentence[this.sentence.length-1];
        this.sentence[this.sentence.length-1] = null;
        this.idSentence = idSentence;
    }

    public int[] getIdSentence() {
        return this.idSentence;
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
     *
     * @return one strinf full of parts of array
     */
    /**
     * readSyntax() read syntax
     */
    public void readSyntax() {
        try {
            File file = new File("." + File.separator + "src" + File.separator + "ff.syntax.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            //pomocne pole pro zapamatovani hodnot ff.syntax.x
            int[] array = new int[this.idSentence.length];
            Arrays.fill(array, -1);

            NodeList syntaxes = doc.getElementsByTagName("syntax");
            for (int i = 0; i < syntaxes.getLength(); i++) {
                Element syntax = (Element) syntaxes.item(i);

                //zabyvat se jenom temi co maji status 1
                if (syntax.hasAttribute("status") && Integer.parseInt(syntax.getAttribute("status")) == 1
                    && !"clause".equals(syntax.getAttribute("tag"))) {
                    Integer indicator = lastIndex(syntax.getAttribute("nite:id")); //hodnosta ff.syntax.x
                   
                    NodeList childs = syntax.getElementsByTagName("nite:child");
                    for(int k = 0; k < childs.getLength(); k++) {
                        Element child = (Element) childs.item(k);
                        String href = child.getAttribute("href");
                       
                        //parser href
                        int[] pom = parser(href);
                        int from = pom[0];
                        int to = pom[1];

                        //for skonci kdyz je from vetsi nez tail, tzn. uz jsme u dalsi vety
                        //if(from > this.tail) break;
                        //slouceni skupin do poli k sobe
                        int indexTo = 0;
                        int indexFrom = 0;
                        int ok = 0;
                        if (from != to + 1) {
                            int j;

                            //nalezeni, kde indexu kde zacina a konci cast skupiny v sentence
                            for (j = 0; j < tail - head; j++) {
                                if (idSentence[j] == to) {
                                    indexTo = j;
                                    ok++;
                                }
                                if (idSentence[j] == from) {
                                    indexFrom = j;
                                }
                            }

                            //ulozeni vsech podcasti do jedne
                            while (indexFrom != indexTo) {
                                sentence[indexTo - 1] = sentence[indexTo - 1] + " " + sentence[indexTo];
                                sentence[indexTo] = null;
                                indexTo--;
                            }
                        }
                    

                        //kvuli vypisu
                        
                        int j;
                        for (j = 0; j < tail - head; j++) {
                            if (idSentence[j] == to) {
                                indexTo = j;
                            }
                            if (idSentence[j] == from) {
                                indexFrom = j;
                            }
                        }

                        //ulozeni id z ff.syntax.x
                        if (ok > 0) {
                            array[indexFrom] = indicator;
                        }
                    }
                    //System.out.println(sentence[indexFrom]); 
                }
            }

            //zkopirovat pomocneho pole do idSentence, od ted je v idSentence pripraveno na relation
            System.arraycopy(array, 0, idSentence, 0, array.length);
            System.out.println(Arrays.toString(idSentence));
            System.out.println(Arrays.toString(sentence));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int lastIndex(String str) {
        return Integer.parseInt(str.split("\\.")[2]);
    }

    public void readRelation() {
        try {
            File file = new File("." + File.separator + "src" + File.separator + "ff.syntax-relation.xml");
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

            //pole idSentence obsahuje -1 pokud slovo na teto pozici v sentence nikam neukazuje
            //a hodnotu 0-(delka vety), pokud slovo někam ukazuje
            //tzn. v poli idSentence je uloženo na pozici 0, hodnota 3 znamena ze slovo nebo
            //skupina slov na pozici 0 v sentence ukazuje na slovo nebo skupinu slov na pozici
            //3 v sentence, tj. sentence[3]
            System.arraycopy(array, 0, idSentence, 0, array.length);
            System.out.println(Arrays.toString(idSentence));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Funkce na parsovani href
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

    public void createDocument() throws FileNotFoundException, IOException {
        DOMImplementation domi = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = domi.createDocument(svgNS, "svg", null);
        Element svgRoot = doc.getDocumentElement();
        svgRoot.setAttributeNS(null, "width", "100%");
        svgRoot.setAttributeNS(null, "heigth", "100%");
        svgRoot.appendChild(createDefs(doc, svgNS));
        int y = 100;
        int x = 10;
        int counter = 0;
        for (String str : sentence) {
            if (str != null) {
                Element phrase = doc.createElementNS(svgNS, "text");
                phrase.setAttributeNS(null, "id", Integer.toString(counter));
                phrase.setAttributeNS(null, "y", Integer.toString(y));
                phrase.setAttributeNS(null, "x", Integer.toString(x));
                phrase.setAttributeNS(null, "style","font-size: 16px;"); //Nebudeme nič rátať, dáme to tam natvrdo a máme hotovson
                str = "[" + str + "]";
                x += str.length() * 7; //Neviem prečo, ale je to takto pekne, aj s 8čkou to ide dobre
                phrase.setTextContent(str);
                svgRoot.appendChild(phrase);
            }
            counter++;
        }
        for (int i = 0; i < idSentence.length; i++) {
            if (idSentence[i] != - 1) {
                Element from = doc.getElementById(Integer.toString(i));
                Element to = doc.getElementById(Integer.toString(idSentence[i]));
                int fromXcor = Integer.parseInt(from.getAttribute("x"));
                int fromYcor = Integer.parseInt(from.getAttribute("y"));
                int toXcor = Integer.parseInt(to.getAttribute("x"));
                int toYcor = Integer.parseInt(to.getAttribute("y"));
                float aCx = fromXcor + 21.3561706542969f;
                String curvedPath = "M " + fromXcor + " " + fromYcor + "A " + aCx + " " + aCx + " 0 0 1 " + toXcor + " " + toYcor;
                Element path = doc.createElementNS(svgNS, "path");
                path.setAttributeNS(null, "d", curvedPath);
                path.setAttributeNS(null, "stroke", ColorConverter.cmykToHex(0f, 0f, 0f, 1f));
                path.setAttributeNS(null, "style", "fill: none; stroke-width: 1px; vector-effect: non-scaling-stroke; marker-start: url(#Circle); marker-end: url(#Triangle); stroke: #000000");
                svgRoot.appendChild(path);
            }
        }

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("out.xml"));
            t.transform(source, result);
        } catch (TransformerConfigurationException tce) {
            tce.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Vytvori tvar konca sipky
     *
     * @param doc
     * @param svgNS
     * @return
     */
    private Element createDefs(Document doc, String svgNS) {
        Element defs = doc.createElementNS(svgNS, "defs");
        Element markerTriangle = doc.createElementNS(svgNS, "marker");
        markerTriangle.setAttributeNS(null, "id", "Triangle");
        markerTriangle.setAttributeNS(null, "viewBox", "-15 -5 20 20");
        markerTriangle.setAttributeNS(null, "refX", "-3");
        markerTriangle.setAttributeNS(null, "refY", "0");
        markerTriangle.setAttributeNS(null, "markerWidth", "16");
        markerTriangle.setAttributeNS(null, "markerHeight", "20");
        markerTriangle.setAttributeNS(null, "orient", "auto");
        Element path = doc.createElementNS(svgNS, "path");
        path.setAttributeNS(null, "d", "M -15 -5 L 0 0 L -15 5 z");
        markerTriangle.appendChild(path);
        Element markerCircle = doc.createElementNS(svgNS, "marker");
        markerCircle.setAttributeNS(null, "id", "Circle");
        markerCircle.setAttributeNS(null, "viewBox", "-5 -5 10 10");
        markerCircle.setAttributeNS(null, "refX", "0");
        markerCircle.setAttributeNS(null, "refY", "0");
        markerCircle.setAttributeNS(null, "markerWidth", "6");
        markerCircle.setAttributeNS(null, "markerHeight", "6");
        markerCircle.setAttributeNS(null, "orient", "auto");
        Element circle = doc.createElementNS(svgNS, "circle");
        circle.setAttributeNS(null, "cx", "0");
        circle.setAttributeNS(null, "cy", "0");
        circle.setAttributeNS(null, "r", "5");
        //circle.setAttributeNS(null, "fill", "black");
        markerCircle.appendChild(circle);
        defs.appendChild(markerTriangle);
        defs.appendChild(markerCircle);
        return defs;

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

    

}
