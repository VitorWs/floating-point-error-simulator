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
import apresentacao.Formatador;
import java.util.List;
/**
 *
 * @author laeds
 */
public class Cli {
    private static Scanner entrada = new Scanner(System.in);
    private static final String FORMATO_LINHA = "%-15s | %-17s | %13s | %13s | %13s | %13s";
    
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

        // 2) Calcular nos dois modos e, so se nao houver erro, mostrar a tabela
        ModoCorte[] modos = ModoCorte.values();
        try {
            ResultadoOperacao[] resultados = new ResultadoOperacao[modos.length];
            for (int i = 0; i < modos.length; i++) {
                resultados[i] = Simulador.executar(x, y, op, n, modos[i]);
            }

            System.out.println("");
            System.out.println(x + " " + op.getSimbolo() + " " + y + "   (n = " + n + ")");
            imprimirCabecalho("Modo");
            for (int i = 0; i < modos.length; i++) {
                imprimirLinha(modos[i].toString(), resultados[i]);
            }

            // Operandos depois do corte: mostra por que os modos divergem.
            System.out.println("");
            System.out.println("Operandos armazenados com " + n + " digitos:");
            for (int i = 0; i < modos.length; i++) {
                System.out.println(modos[i] + ": x = " + resultados[i].x()
                        + " | y = " + resultados[i].y());
            }
            System.out.println("");
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
    
    private static void imprimirCabecalho(String tituloPrimeiraColuna) {
            String cabecalho = String.format(FORMATO_LINHA,
                tituloPrimeiraColuna, "Precisao finita", "Exato", "Aproximado",
                "Erro absoluto", "Erro relativo");
            System.out.println(cabecalho);
            System.out.println("-".repeat(cabecalho.length()));
    }
    
    private static void imprimirLinha(String rotulo, ResultadoOperacao r) {
    System.out.println(String.format(FORMATO_LINHA,
            rotulo,
            r.resultado(),
            Formatador.formatar(r.exato()),
            Formatador.formatar(r.aproximado()),
            Formatador.formatar(r.erroAbsoluto()),
            Formatador.formatar(r.erroRelativo())));
    }
    
    public static void executarSomaSucessiva(){
        String x = lerNumero("Digite o numero a ser somado: ");
        int vezesSomadas = lerInteiro("Digite quantas vezes o numero sera somado: ", 1);
        int digitosSignificativos = lerInteiro("Digitos significativos (n): ", 1);
        
        for(ModoCorte modo: ModoCorte.values()){
            List<ResultadoOperacao> lista = Simulador.somaSucessiva(x, vezesSomadas, digitosSignificativos, modo);
            
            System.out.println("");
            System.out.println(modo);
            imprimirCabecalho("Passo"); 
            
            for (int i = 0; i < lista.size(); i++) {
                imprimirLinha(String.valueOf(i + 1), lista.get(i));
            }
            
            ResultadoOperacao ultimo = lista.get(lista.size() - 1);
            System.out.println("Erro total apos " + vezesSomadas + " somas: Ea = "
                + Formatador.formatar(ultimo.erroAbsoluto())
                + " | Er = " + Formatador.formatar(ultimo.erroRelativo()));
        }
        
        System.out.println("");
            
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
                
                executarSomaSucessiva();
                
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
