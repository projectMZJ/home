/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic.bush;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Šimon Priadka
 */
public class ColorConverter {
    
    
    /**
     * Returns hexadecimal representation for given colour in hexadecimal format, that is gonna be used firther in repersentation of the data
     * 
     * @param cyan
     * @param magenta
     * @param yellow
     * @param black
     * @return 
     */
    public static String cmykToHex(float cyan, float magenta, float yellow, float black) {
        int r = (int) (255 * (1 - cyan) * (1 - black));
        int g = (int) (255 * (1 - magenta) * (1 - black));
        int b = (int) (255 * (1 - yellow) * (1 - black));
        String hex = "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
        System.out.println(hex);
        return hex;
    }

}
