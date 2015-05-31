/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pb138.syntacticbush;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class for creation probability.
 * @author pražák
 */
public class Evaluation {

    private List<Sentence> evaluationSentence; /** save object from class Sentence */
    private List<String[]> listSentence; /** sentence for SVG*/
    private List<Integer> probabilitySentence;/** probability of sentence */
    private List<List<Integer[][]>> relation; /** probability and relation saved in 2d array for SVG */
    private int denominator; /** denominator for probability */

    /**
     * Constructor for creation probability.
     * @param e list with object from class Sentence
     */
    public Evaluation(List<Sentence> e) {
        this.listSentence = new ArrayList<>();
        this.probabilitySentence = new ArrayList<>();
        this.evaluationSentence = e;
        this.denominator = e.size();
        this.relation = new ArrayList<>();
        compareSentence();
        compareRelation();
    }

    /**
     * Function which return list of sentence, Size of list is number of pictures in SVG.
     *
     * @return listSentence
     */
    public List<String[]> getListSentence() {
        return listSentence;
    }

     /**
     * Function return probability of evaluated sentence
     * 
     * @return probabilitySentence
     */
    public List<Integer> getProbabilitySentence() {
        return probabilitySentence;
    }

     /**
     * Function return denominator of probability.
     * @return denominator
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Return list, each item of list is another list with length of sentences in Sentence,
     * which contain 2D array
     * array[number of evaluator of sentence][2] contains pair idSentence and probability.
     * Example: array[0][0] - idSentence[0], array[0][1] - probability of arrow
     * 
     * @return relation
     */
    public List<List<Integer[][]>> getRelation() {
        return relation;
    }
    
    /**
     * Creates text element of the document
     * @author Šimon Priadka
     * @param doc Document from wich is going to be created
     * @param ns Namespace of the document
     * @param root Element where the text elements are going to be appended
     */
    private void createTokens(Document doc, String ns, Element root) {
        int totalWidth = 1000;
        int x = 10;
        int y = denominator * 75;
        for (int user = 0; user < getListSentence().size(); user++) {
            int counter = 0;
            List<Integer> notTogether = evaluationSentence.get(user).getNotTogetherWord();
            float nom = probabilitySentence.get(user);
            float denom = denominator;
            for (String s : getListSentence().get(user)) {
                if (s != null && (!s.contains("null"))) {
                    Element token = doc.createElementNS(ns, "text");
                    token.setAttributeNS(null, "id", user + "sentence" + counter);
                    if (x >= totalWidth - 50) {
                        x = 10;
                        y += 50;
                    }
                    token.setAttributeNS(null, "x", Integer.toString(x));
                    token.setAttributeNS(null, "y", Integer.toString(y));
                    if (notTogether.contains(counter)) {
                        token.setAttributeNS(null, "style", "font-size: 12px; opacity: 0; stroke: #f07e7e");
                    } else {
                        token.setAttributeNS(null, "style", "font-size: 12px; opacity: 0; stroke:" + ColorConverter.cmykToHex(0f, 0f, 0f, nom / denom));
                    }
                    s = "[" + s + "]";
                    x += s.length() * 6;
                    token.setTextContent(s);
                    createAnimation(doc, ns, token);
                    root.appendChild(token);
                }
                counter++;
            }
            Element prob = doc.createElementNS(ns, "text");
            if (x >= totalWidth - 50){
                x = 10;
                y += 50;
            }
            prob.setAttributeNS(null, "x", Integer.toString(x));
            prob.setAttributeNS(null, "y", Integer.toString(y));
            prob.setAttributeNS(null, "style", "font-size: 12px; opacity: 0; stroke:" + ColorConverter.cmykToHex(0f, 0f, 0f, nom / denom));
            prob.setTextContent("[" + (int) nom + "/" + (int) denom + "]");
            createAnimation(doc, ns, prob);
            root.appendChild(prob);
            y += 100;
            x = 10;
        }
    }
    
    
    /**
     * Creates paths that are going to represent relation between words
     * @author Šimon Priadka
     * @param doc Document from which is going to be created
     * @param ns Namespace of the document
     * @param root Element to be appended to
     */
    private void createRelation(Document doc, String ns, Element root) {
        int idPath = 0;
        for (int user = 0; user < relation.size(); user++) {
            for (int word = 0; word < relation.get(user).size(); word++) {
                Element from = doc.getElementById(user + "sentence" + word);
                Integer relationArr[][] = relation.get(user).get(word);

                for (int i = 0; i < relationArr.length; i++) {
                    if (relationArr[i][0] != null) {
                        if (relationArr[i][0] != -1) {
                            Element to = doc.getElementById(user + "sentence" + relation.get(user).get(word)[i][0]);
                            int fromXcor = Integer.parseInt(from.getAttribute("x")) + ((from.getTextContent().length() / 2) * 7);
                            int toXcor = Integer.parseInt(to.getAttribute("x")) + ((to.getTextContent().length() / 2) * 7);
                            int fromYcor = Integer.parseInt(from.getAttribute("y"));
                            int toYcor = Integer.parseInt(to.getAttribute("y"));
                            if (fromXcor < toXcor) {

                                fromYcor -= 10;
                                toYcor -= 10;
                            } else {
                                fromYcor += 10;
                                toYcor += 10;
                            }
                            float aCx = fromXcor + 21.3561706542969f;
                            float aCy = aCx;
                            float nom = relationArr[i][1];
                            float denom = denominator;
                            String curvedPath;
                            if ((toXcor - fromXcor) > 400){
                                aCy = 11.3561706542969f;
                            }
                            curvedPath = "M " + fromXcor + " " + fromYcor + " A " + aCx + " " + aCy + " 0 0 1 " + toXcor + " " + toYcor;
                            Element path = doc.createElementNS(ns, "path");
                            path.setAttributeNS(null, "id", Integer.toString(idPath));
                            path.setAttributeNS(null, "d", curvedPath);
                            path.setAttributeNS(null, "style", "fill: none; stroke-width: 1px; vector-effect: non-scaling-stroke; marker-start: url(#Circle); marker-end: url(#Triangle); stroke:" + ColorConverter.cmykToHex(0f, 0f, 0f, nom / denom));
                            root.appendChild(path);
                            Element relationProb = doc.createElementNS(ns, "text");
                            relationProb.setAttributeNS(null, "style", "font-size: 8px;");
                            Element alongPath = doc.createElementNS(ns, "textPath");
                            alongPath.setAttributeNS(null, "xlink:href", "#" + idPath);
                            alongPath.setAttributeNS(null, "startOffset", "50%");
                            alongPath.setAttributeNS(null, "text-anchor", "middle");
                            alongPath.setAttributeNS(null, "style", "stroke: " + ColorConverter.cmykToHex(0f, 0f, 0f, nom / denom) + ";");
                            Element tspan = doc.createElementNS(ns, "tspan");
                            tspan.setAttributeNS(null, "dy", "-5");
                            
                            tspan.setTextContent("[" + relationArr[i][1] + "/" + denominator + "]");
                            alongPath.appendChild(tspan);
                            relationProb.appendChild(alongPath);
                            root.appendChild(relationProb);
                            idPath++;

                        }
                    }
                }
            }
        }

    }
    
    /**
     * Creates animation of the toBeAnimated element
     * @author Šimon Priadka
     * @param doc Document from which is going to be created
     * @param ns Namespace of the document
     * @param toBeAnimated Element to be animated
     */
    private void createAnimation(Document doc, String ns, Element toBeAnimated) {
        Element animate = doc.createElementNS(ns, "animate");
        animate.setAttributeNS(null, "atributeType", "CSS");
        animate.setAttributeNS(null, "attributeName", "opacity");
        animate.setAttributeNS(null, "repeatCount", "1");
        animate.setAttributeNS(null, "from", "0");
        animate.setAttributeNS(null, "to", "1");
        animate.setAttributeNS(null, "dur", "1s");
        animate.setAttributeNS(null, "fill", "freeze");
        toBeAnimated.appendChild(animate);

    }
    
    /**
     * Creates document, that is going to be visualized
     * @author Šimon Priadka
     * @return document
     */
    public Document createDocument() {
        DOMImplementation domi = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = domi.createDocument(svgNS, "svg", null);
        Element svgRoot = doc.getDocumentElement();
        int totalHeight = 400;
        svgRoot.setAttributeNS(null, "width", "100%");
        svgRoot.setAttributeNS(null, "heigth", "100%");
        svgRoot.setAttributeNS(null, "style", "width: 1000px; height: " + totalHeight + "px;");
        svgRoot.setAttributeNS(null, "onload", "drawPaths(getAllPaths())");
        svgRoot.setAttributeNS(null, "viewBox", "0 0 1000 " + totalHeight);
        svgRoot.appendChild(createDefs(doc, svgNS));
        createTokens(doc, svgNS, svgRoot);
        createRelation(doc, svgNS, svgRoot);
        return doc;
    }

    /**
     * Creates definitions of the marker, that are going to be used further in the code
     * @author Šimon Priadka
     * @param doc Document from which is going to be created
     * @param svgNS Namespace of the element
     * @return Element of the definitions
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

    /**
     * Method with fill array arrayOfPro with value of probability and idSentence.
     *
     * @param arrayOfpro array idSentence and probability
     * @param length arrayOfpro[length][2]
     * @param position in idSentence, which we want now
     * @param index array of index, where sentence have same dividing
     *
     * @return fill arrayOfPro
     */
    public Integer[][] fillArray(Integer[][] arrayOfpro, int length, int position, int[] index) {
        int[] indexID = getEvaluationSentence().get(index[0]).getIdSentence();

        arrayOfpro[0][0] = indexID[position];
        arrayOfpro[0][1] = 1;

        int i = 1;

        for (int j = 1; j < probabilitySentence.get(length); j++) {
            //System.out.println(probabilitySentence.get(length));
            indexID = getEvaluationSentence().get(index[j]).getIdSentence();
            int counter = 0;
            for (int k = 0; k < i; k++) {
                if (arrayOfpro[k][0] == indexID[position]) {
                    arrayOfpro[k][1]++;
                    counter++;
                }
            }
            if (counter == 0) {
                arrayOfpro[j][0] = indexID[position];
                arrayOfpro[j][1] = 1;
            }
        }                
        return arrayOfpro;
    }

    /**
     * Method for counting probability of arrows
     */
    private void compareRelation() {
        int evaluator = 0;

        for (String[] list_sentence1 : getListSentence()) {
            List<Integer[][]> list_probabilities = new ArrayList<>();

            int counter = 0;
            int[] index = new int[10]; //indexy vet v jednom listSentence
            Arrays.fill(index, -1);

            for (int i = 0; i < getEvaluationSentence().size(); i++) {
                if (Arrays.equals(getListSentence().get(evaluator), getEvaluationSentence().get(i).getSentence())) {
                    index[counter] = i;
                    counter++;
                }
            }
            //System.out.println(evaluationSentence.get(0).getIdSentence().length);
            for (int i = 0; i < getEvaluationSentence().get(0).getIdSentence().length; i++) {
                Integer[][] arrayOfpro = new Integer[probabilitySentence.get(evaluator)][2];
                arrayOfpro = fillArray(arrayOfpro, evaluator, i, index);

                //pro vypis arrayfOfpro
                for (int m = 0; m < probabilitySentence.get(evaluator); m++) {
                    System.out.print(arrayOfpro[m][0]);
                    System.out.print(" ");
                    System.out.print(arrayOfpro[m][1]);
                    System.out.print("\n");

                }

                System.out.println("");
                list_probabilities.add(arrayOfpro);
            }
            evaluator++;
            relation.add(list_probabilities);
        }

    }

    /**
     * Methods for counting probability and dividing of sentence according to evalution.
     */
    private void compareSentence() {
        getListSentence().add(getEvaluationSentence().get(0).getSentence());
        probabilitySentence.add(1);
        int size = getEvaluationSentence().size();

        int i = 1;
        int counter = 0;
        while (size > 1) {
            for (int j = 0; j < i; j++) {

                if (Arrays.equals(getListSentence().get(j), (getEvaluationSentence().get(i).getSentence()))) {
                    probabilitySentence.set(j, probabilitySentence.get(j) + 1);
                    counter++;
                }
            }
            if (counter == 0) {
                probabilitySentence.add(1);
                getListSentence().add(getEvaluationSentence().get(i).getSentence());
                i++;
            }
            size--;
        }
    }

    /**
     * @return the evaluationSentence
     */
    public List<Sentence> getEvaluationSentence() {
        return evaluationSentence;
    }
}
