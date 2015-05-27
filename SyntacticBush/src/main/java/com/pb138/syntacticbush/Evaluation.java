/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pb138.syntacticbush;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Trida pro vytvoreni pravdepodobnosti.
 * @author filip
 */
public class Evaluation {
    List<Sentence> evaluationSentence; /** ulozeni objektu tridy Sentence */
    List<String[]> list_sentence; /** vety pro SVG*/
    List<Integer> probability_sentence; /** pravdepodobnost vet */
    List<List<Integer[][]>> relation; /** pravdepodobnost a relation ulozene ve 2d poli pro SVG */
    int denominator; /** jmenovatel pro pravdepodobnost */

    /**
     * Konstruktor pro vytvoreni pravdepodobnosti.
     * @param e list s prvky tridy Sentence
     */
    public Evaluation(List<Sentence> e) {
        this.list_sentence = new ArrayList<>();
        this.probability_sentence = new ArrayList<>();
        this.evaluationSentence = new ArrayList<>();
        this.evaluationSentence = e;
        this.denominator = e.size();
        this.relation = new ArrayList<List<Integer[][]>>();
    }
    /**
     * Funkce vraci list vet, Size listu udava pocet obrazku SVG.
     * @return list_sentence
     */
    public List<String[]> getListSentence() {
        return list_sentence;
    }
    /**
     * Funkce vraci pravdepodobnost ohodnoceni vet
     * Prvnich n-1 prvku jsou pravdepodobnosti na pozicich
     * stejnych jako listSentence.
     * @return probability_sentence
     */
    public List<Integer> getProbabilitySentence() {
        return probability_sentence;
    }
    /**
     * Funkce vrati jmenovatel pravdepodobnosti.
     * @return denominator
     */
    public int getDenominator() {
        return denominator;
    }
    /**
     * Vrati list ktery ma delku poctu SVG obrazku, kazda polozka listu
     * je dalsi list delky vety v Sentence, ktere postupne obsahuji 2D pole
     * pole[pocet ohodnotitelu vety][2] obsahuje dvojice idSentence a pst.
     * Priklad: pole[0][0] - idSentence[0]
     * pole[0][1] - pst sipky
     * 
     * @return relation
     */
    public List<List<Integer[][]>> getRelation() {
        return relation;
    }
    
   /**
    * Naplni pole arrayOfpro hodnotami pst a idsentence.
    * 
    * @param arrayOfpro pole idsentence a pst
    * @param length arrayOfpro[length][2]
    * @param position pozice v idSentence, kterou zrovna chceme
    * @param index pole indexu kde jsou vety stejne rozdelene
    * 
    * @return vyplnene pole
    */
    public Integer[][] fillArray(Integer[][] arrayOfpro, int length, int position, int[] index) {
        int[] indexID = evaluationSentence.get(index[0]).getIdSentence();
    
        arrayOfpro[0][0] = indexID[position];
        arrayOfpro[0][1] = 1;
            
        int i = 1;
        
        for (int j = 1; j < probability_sentence.get(length); j++) {
            //System.out.println(probability_sentence.get(length));
            indexID = evaluationSentence.get(index[j]).getIdSentence();
            int counter = 0;
            for (int k = 0; k < i; k++) {
                if (arrayOfpro[k][0] == indexID[position]) {   
                    arrayOfpro[k][1]++;
                    counter++;
                }
            } 
            if(counter == 0) {
                arrayOfpro[j][0] = indexID[position];
                arrayOfpro[j][1] = 1;  
            }        
        }    
      /*  for(int m = 0; m < probability_sentence.get(length); m++) {
                    System.out.print(arrayOfpro[m][0]);
                    System.out.print(" ");
                    System.out.print(arrayOfpro[m][1]);
                    System.out.print("\n");
                    
                
                } */
                    
        return arrayOfpro;
    }
    /**
     * Metoda pro spocitani pravdepodobnosti sipek
     */
    public void compareRelation() {
        int evaluator = 0;
        
        for (String[] list_sentence1 : list_sentence) {
            List<Integer[][]> list_probabilities = new ArrayList<>();
            
            int counter = 0;
            int[] index = new int[10]; //indexy vet v jednom list_sentence
            Arrays.fill(index, -1);
            
            for (int i = 0; i < evaluationSentence.size(); i++) {
                if (Arrays.equals(list_sentence.get(evaluator), evaluationSentence.get(i).getSentence())) {
                    index[counter] = i;
                    counter++;
                }
            }
                //System.out.println(evaluationSentence.get(0).getIdSentence().length);
            for (int i = 0; i < evaluationSentence.get(0).getIdSentence().length; i++) {
                Integer[][] arrayOfpro = new Integer[probability_sentence.get(evaluator)][2];
                arrayOfpro = fillArray(arrayOfpro, evaluator, i, index);
                
                //pro vypis arrayfOfpro
                for(int m = 0; m < probability_sentence.get(evaluator); m++) {
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
     * Metoda pro spocitani pravdepodnosti a rozdeleni jednotlivych vet
     * podle ohodnoceni.
     */
    public void compareSentence() {
        list_sentence.add(evaluationSentence.get(0).getSentence());
        probability_sentence.add(1);
        int size = evaluationSentence.size();
        
        int i = 1;
        int counter = 0;
        while (size > 1) {
            for (int j = 0; j < i; j++) {
          
                if (Arrays.equals(list_sentence.get(j),(evaluationSentence.get(i).getSentence()))) {   
                    probability_sentence.set(j, probability_sentence.get(j)+1);
                    counter++;
                }
            }    
            if (counter == 0) {
                probability_sentence.add(1);
                list_sentence.add(evaluationSentence.get(i).getSentence());
                i++;
            }
            size--;
        }
    }
}
