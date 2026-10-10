/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apresentacao;

import java.util.Locale;

/**
 * Converte números em texto para exibição, com vírgula decimal e número fixo
 * de dígitos significativos. Isso esconde o ruído de {@code double} que
 * aparece nos últimos dígitos.
 *
 * @author laeds
 */
public final class Formatador {
    
    /* Região usada na formatação (separador decimal: vírgula). */
    private static final Locale LOCALE = Locale.forLanguageTag("pt-BR");
     /* Quantidade de dígitos significativos exibidos. */
    private static final int DIGITOS = 6;
    
    
    private Formatador(){
        
    }
    
     /**
     * Formata um número para exibição.
     *
     * @param valor número a formatar
     * @return "Indefinido" se for NaN, "0" se for zero, ou o número com
     *         {@code DIGITOS} dígitos significativos
     */
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
