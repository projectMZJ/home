/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic.bush;

/**
 *
 * @author omacejovsky
 */
public class Sentence {
    private Integer tail;   
    private Integer head;
    private String[] sentence;
    
    public Sentence(Integer tail, Integer head, String[] sentence)
    {
        this.tail = tail;
        this.head = head;
        this.sentence = sentence;
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
    
    public String toString(){
        String result = "";
        for (int i=0; i < sentence.length; i++){
            result += sentence[i];
            result += " ";
        }
        return result;
    }
   
    
}
