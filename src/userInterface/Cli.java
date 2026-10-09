/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userInterface;


import java.math.BigDecimal;
import java.util.Scanner;
import nucleo.ModoCorte;
import nucleo.Operacao;
import nucleo.ResultadoOperacao;
import nucleo.Simulador;
/**
 *
 * @author laeds
 */
public class Cli {
    private static Scanner entrada = new Scanner(System.in);
    
    public static void mostrarMenu(){
        System.out.println("|| Calculadora! ||");
        System.out.println("");
        System.out.println("- Escolha uma opcao: -");
        System.out.println("");
        System.out.println("1. Operacao unica");
        System.out.println("2. Soma sucessiva");
        System.out.println("3. Sair");
        System.out.println("");
    }
    
    public static String lerLinha(){
        return entrada.nextLine().trim();
    }
    
    public static void executarOperacaoUnica(){
        // 1) Coletar as entradas (cada uma repete até ser válida)
        String x = lerNumero("Digite x: ");
        String y = lerNumero("Digite y: ");
        Operacao op = lerOperacao();
        int n = lerInteiro("Digitos significativos (n): ", 1);

        // 2) Executar nos dois modos e mostrar
        try {
            for (ModoCorte modo : ModoCorte.values()) {
                ResultadoOperacao r = Simulador.executar(x, y, op, n, modo);
                System.out.println(modo);
                System.out.println("");
                System.out.println("Resultado em precisao finita: " + r.resultado());
                System.out.println("Resultado exato: " + r.exato());
                System.out.println("Resultado aproximado: " + r.aproximado());
                System.out.println("Erro absoluto: " + r.erroAbsoluto() + " Erro relativo: " + r.erroRelativo());
                System.out.println("");
            }
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static String lerNumero(String mensagem){
        while(true){
            System.out.print(mensagem);
            String texto = lerLinha();
            
            try{
                BigDecimal bigDecimal = new BigDecimal(texto.replace(',', '.'));
                return texto;
            } catch (NumberFormatException e){
                System.out.println("Valor inválido. Use números como 0,76545.");
            }
        }
    }
    
    public static int lerInteiro(String mensagem, int valorMinimo){
        while(true){
            System.out.print(mensagem);
            String texto = lerLinha();
            
            try{
                int numero = Integer.parseInt(texto);
                
                if(numero >= valorMinimo){
                    return numero;
                }else{
                    System.out.println("Numero menor que o minimo (" + valorMinimo + ")." );
                }
            } catch (NumberFormatException e){
                System.out.println("Valor inválido. Use números inteiros, por exemplo 4.");
            }
        }
    }
    
    public static Operacao lerOperacao(){
        
        while(true){
            System.out.print("Operacao (+,-,*,/): ");
            String escolha = lerLinha();
            
            if(escolha.length() == 1){

                for(Operacao operacao: Operacao.values()){
                    if(operacao.getSimbolo() == escolha.charAt(0)){
                        return operacao;
                    }
                    
                }
                
                System.out.println("Digite uma opecao valida.");
                
            } else{
                System.out.println("Digite uma unica operacao sem espacos.");
            }
        }
    }
    
    public static void main(String[] args) {
        boolean continuar = true;
        
        do{
        mostrarMenu();
        
        switch(lerLinha()){
            
            case "1":
                
                executarOperacaoUnica();
                
                break;
            case "2":
                
                System.out.println("opcao 2 ainda nao implementada");
                
                break;
            case "3":
                
                System.out.println("Fechando...");
                continuar = false;
                
                break;
            default:
                
                System.out.println("Opcao invalida");
                
                break;
        }
        
        }while(continuar);
        
        entrada.close();
    }
}
