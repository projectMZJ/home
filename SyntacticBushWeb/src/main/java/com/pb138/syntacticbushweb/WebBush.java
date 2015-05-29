/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pb138.syntacticbushweb;

import com.pb138.syntacticbush.Evaluation;
import com.pb138.syntacticbush.Sentence;
import com.pb138.syntacticbush.SyntacticBush;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author Game
 */
public class WebBush {

    private Map<String, File> dataDir = new HashMap<>();
    private File dataLoc = new File("D:\\Documents\\PB138\\SB\\SyntacticBushWeb\\data");
    
    private List<Sentence> getEvaluationAtIndex(String data, int index) {

        List<Sentence> result = new ArrayList<>();
        try {
            SyntacticBush bush = new SyntacticBush(new File(dataLoc.getAbsolutePath() + File.separator + data + File.separator + "user1" + File.separator + "ff.text.xml"));
            File dir = new File(dataLoc.getAbsolutePath() + File.separator + data);
            for (File userDir : dir.listFiles()) {
                Map<String, File> map = new HashMap<>();
                for (File user : userDir.listFiles()) {
                    if (user.getName().equals("ff.text.xml")) {
                        map.put("text", user);
                    } else if (user.getName().equals("ff.syntax.xml")) {
                        map.put("syntax", user);
                    } else if (user.getName().equals("ff.syntax-relation.xml")) {
                        map.put("relation", user);
                    }
                }
                result.add(new Sentence(bush.getAllSentencesIndexes()[index], map.get("text"), map.get("syntax"), map.get("relation")));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        WebBush bush = new WebBush();
        List<Sentence> ls = bush.getEvaluationAtIndex("01", 10);
        for (Sentence s : ls) {
            System.out.println(s.toString());
        }
        System.out.println(bush.dataLoc.getAbsolutePath());

    }

    public Evaluation firstEvaluation(String data) {
        return new Evaluation(getEvaluationAtIndex(data, 0));
    }

    public Evaluation nextSentence(String data, Evaluation currentEvaluation) {
        try {
            int sentenceIndex = currentEvaluation.getEvaluationSentence().get(0).getHead();
            SyntacticBush bush = new SyntacticBush(getTextAtData(data));
            for (int i = 0; i < bush.getAllSentencesIndexes().length; i++) {
                if (bush.getAllSentencesIndexes()[i] == sentenceIndex) {
                    if (i == bush.getAllSentencesIndexes().length - 1) {
                        throw new ArrayIndexOutOfBoundsException("No more sentences to load");
                    }
                    return new Evaluation(getEvaluationAtIndex(data, i + 1));
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }

    }

    private File getTextAtData(String data) {
        try {
            return new File(dataLoc + File.separator + data + File.separator + "user1" + File.separator + "ff.text.xml");
        } catch (Exception ex) {
            return null;
        }

    }

    public Evaluation prevSentence(String data, Evaluation currentEvaluation) {
        try {
            int sentenceIndex = currentEvaluation.getEvaluationSentence().get(0).getHead();
            SyntacticBush bush = new SyntacticBush(getTextAtData(data));
            for (int i = 0; i < bush.getAllSentencesIndexes().length; i++) {
                if (bush.getAllSentencesIndexes()[i] == sentenceIndex) {
                    if (i == 0) {
                        throw new ArrayIndexOutOfBoundsException("No more sentences to load");
                    }
                    return new Evaluation(getEvaluationAtIndex(data, i - 1));
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Evaluation goToSentence(String data, int index) {
        try {
            int realIndex = index - 1;
            SyntacticBush bush = new SyntacticBush(getTextAtData(data));
            if (realIndex < 0 || realIndex > bush.getAllSentencesIndexes().length - 1) {
                throw new ArrayIndexOutOfBoundsException("No such sentences to load");
            } else {
                return new Evaluation(getEvaluationAtIndex(data, realIndex));
            }
        } catch (Exception e) {
            return null;
        }

    }

    public String documentToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (IllegalArgumentException | TransformerException | NullPointerException ex) {
            return "";
        }
    }

    public String displayingSentence(Evaluation e, String data) {
        try {
            SyntacticBush bush = new SyntacticBush(getTextAtData(data));
            for (int i = 0; i < bush.getAllSentencesIndexes().length; i++) {
                if (e.getEvaluationSentence().get(0).getHead() == bush.getAllSentencesIndexes()[i]) {
                    return "Displaying sentence n° " + (i + 1) + " from " + bush.getAllSentencesIndexes().length + " sentences";
                }
            }
            return null;
        } catch (NullPointerException ne) {
            return " ";
        }
    }
}
