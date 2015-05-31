/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pb138.syntacticbush;

/**
 *
 * @author Šimon Priadka
 */
public class ColorConverter {
    
    
    /**
     * Returns hexadecimal representation for given colour in hexadecimal format, that is gonna be used firther in repersentation of the data
     * 
     * @param cyan Percentage of the color cyan in the result color
     * @param magenta Percentage of the color magenta in the result color
     * @param yellow Percentage of the color yelllow in the result color
     * @param black Percentage of the color black in the result color
     * @return hex string of the color based on arguments
     */
    public static String cmykToHex(float cyan, float magenta, float yellow, float black) {
        int r = (int) (255 * (1 - cyan) * (1 - black));
        int g = (int) (255 * (1 - magenta) * (1 - black));
        int b = (int) (255 * (1 - yellow) * (1 - black));
        String hex = "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
        return hex;
    }
    

}
