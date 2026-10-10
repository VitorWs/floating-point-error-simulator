/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apresentacao;

import java.util.Locale;

/**
 *
 * @author laeds
 */
public final class Formatador {
    
    private static final Locale LOCALE = Locale.forLanguageTag("pt-BR");
    private static final int DIGITOS = 6;
    
    
    private Formatador(){
        
    }
    
    public static String formatar(double valor){
        if(Double.isNaN(valor)){
            return "Indefinido";
        }
        if(valor == 0.0){
            return "0";
        }
        return String.format(LOCALE, "%." + DIGITOS + "g", valor);
        
    }
    
}
