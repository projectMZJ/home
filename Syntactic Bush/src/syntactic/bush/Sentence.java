/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic.bush;

import java.io.File;
import java.util.Arrays;
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
            
            //pomocne pole pro zapamatovani hodnot ff.syntax.x
            int[] array = new int[this.idSentence.length];
            Arrays.fill(array, -1);
            NodeList syntaxes = doc.getElementsByTagName("syntax");
            for (int i=0; i < syntaxes.getLength(); i++){
                Element syntax = (Element)syntaxes.item(i);
                
                //zabyvat se jenom temi co maji status 1
                if (syntax.hasAttribute("status") && Integer.parseInt(syntax.getAttribute("status")) == 1){
                    Integer indicator = lastIndex(syntax.getAttribute("nite:id")); //hodnosta ff.syntax.x
                    Element child = (Element)syntax.getElementsByTagName("nite:child").item(0);
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
                    if(from != to+1){
                        int j;
                        
                        //nalezeni, kde indexu kde zacina a konci cast skupiny v sentece
                        for(j = 0; j < tail-head; j++){
                            if(idSentence[j] == to){
                                indexTo = j;
                                ok++;
                            }
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
                    
                    //ulozeni id z ff.syntax.x
                    if(ok > 0) array[indexFrom] = indicator;
                    
                    //System.out.println(sentence[indexFrom]); 
                }
            }
            
            //zkopirovat pomocneho pole do idSentence, od ted je v idSentence pripraveno na relation
            System.arraycopy(array, 0, idSentence, 0, array.length);
            System.out.println(Arrays.toString(idSentence));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public static int lastIndex(String str){
        return Integer.parseInt(str.split("\\.")[2]);
    }
    
    public void readRelation(){
        try {
            File file = new File("." + File.separator + "src" + File.separator + "ff.syntax-relation.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            
            //pomocne pole pro zapamatovani hodnot ff.syntax-relation.x
            int[] array = new int[this.idSentence.length];
            Arrays.fill(array, -1);
            NodeList srelations = doc.getElementsByTagName("srelation");
            for (int i=0; i < srelations.getLength(); i++){
                Element relation = (Element)srelations.item(i);
                
                Element pointer = (Element) relation.getElementsByTagName("nite:pointer").item(0);
                String href = pointer.getAttribute("href");
                
                //parser href
                int[] pom = parser(href);
                int pointerID = pom[0]; //ff.syntax of pointer
                  
                Element child = (Element)relation.getElementsByTagName("nite:child").item(0);
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
                for(j = 0; j < idSentence.length; j++){
                    if(idSentence[j] == pointerID){
                        ok++;
                        indexTo = j;
                    }
                    if(idSentence[j] == childID){
                        indexFrom = j;
                        ok++;
                    }
                }
                
                if (ok > 0) array[indexFrom] = indexTo;
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
     * @param href atribut nite
     * @return pole, array[0] první indikator, pole [1] druhy indikator
     */
    public int[] parser (String href) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher;
        matcher = pattern.matcher(href);
        int[] array = new int[2];
        if (matcher.find()) {
            array[0] = Integer.parseInt(matcher.group(0));
            if(matcher.find()) array[1] = Integer.parseInt(matcher.group(0));
            else array[1] = array[0];
        }
        return array;
    }
}
