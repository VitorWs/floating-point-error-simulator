/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commandLineInteface;


import java.math.BigDecimal;
import java.util.Scanner;
import nucleo.ModoCorte;
import nucleo.Operacao;
import nucleo.ResultadoOperacao;
import nucleo.Simulador;
import apresentacao.Formatador;
import java.util.List;

/*
 * Interface de linha de comando do simulador de erros numéricos.
 * Só cuida de entrada e saída; os cálculos ficam no pacote nucleo.
 *
 * @author laeds
 */
public class Cli {
    /* Leitor único do teclado, usado por toda a classe. */
    private static Scanner entrada = new Scanner(System.in);
    /* Formato de cada linha da tabela (largura das colunas). */
    private static final String FORMATO_LINHA = "%-15s | %-17s | %13s | %13s | %13s | %13s";
    
     /* Mostra o menu principal. */
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
    
    /* Lê uma linha do teclado, sem espaços nas pontas. */
    public static String lerLinha(){
        return entrada.nextLine().trim();
    }
    
    
    /*
     * Opção 1: calcula x (op) y com truncamento e arredondamento e mostra
     * a tabela comparativa.
     */
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
            // Por exemplo, divisão por zero.
            System.out.println(e.getMessage());
        }
    }
    
    
    /*
     * Pede um número (vírgula ou ponto) até receber um valor válido.
     * Devolve o texto digitado, pois o núcleo trabalha com texto.
     */
    public static String lerNumero(String mensagem){
        while(true){
            System.out.print(mensagem);
            String texto = lerLinha();
            
            try{
                 // Criado só para validar o texto.
                BigDecimal bigDecimal = new BigDecimal(texto.replace(',', '.'));
                return texto;
            } catch (NumberFormatException e){
                System.out.println("Valor inválido. Use números como 0,76545.");
            }
        }
    }
    
    
    /* Pede um inteiro até receber um valor válido, maior ou igual a valorMinimo. */
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
    
    
     /* Pede o símbolo de uma operação (+, -, *, /) até receber um válido. */
    public static Operacao lerOperacao(){
        
        while(true){
            System.out.print("Operacao (+,-,*,/): ");
            String escolha = lerLinha();
            
            // Só usa charAt depois de garantir que há exatamente 1 caractere.
            if(escolha.length() == 1){

                for(Operacao operacao: Operacao.values()){
                    if(operacao.getSimbolo() == escolha.charAt(0)){
                        return operacao;
                    }
                    
                }
                
                System.out.println("Digite uma opcao valida.");
                
            } else{
                System.out.println("Digite uma unica operacao sem espacos.");
            }
        }
    }
    
    /* Imprime o cabeçalho da tabela e uma linha de traços. */
    private static void imprimirCabecalho(String tituloPrimeiraColuna) {
            String cabecalho = String.format(FORMATO_LINHA,
                tituloPrimeiraColuna, "Precisao finita", "Exato", "Aproximado",
                "Erro absoluto", "Erro relativo");
            System.out.println(cabecalho);
            System.out.println("-".repeat(cabecalho.length()));
    }
    
     /* Imprime uma linha da tabela;  rotulo é o modo ou o número do passo. */
    private static void imprimirLinha(String rotulo, ResultadoOperacao r) {
    System.out.println(String.format(FORMATO_LINHA,
            rotulo,
            r.resultado(),
            Formatador.formatar(r.exato()),
            Formatador.formatar(r.aproximado()),
            Formatador.formatar(r.erroAbsoluto()),
            Formatador.formatar(r.erroRelativo())));
    }
    
    
    /*
     * Opção 2: soma um valor várias vezes, cortando o acumulado a cada passo,
     * e mostra uma tabela por modo com o erro total no final.
     */
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
            
            // O último elemento é o acumulado final (erro total).
            ResultadoOperacao ultimo = lista.get(lista.size() - 1);
            System.out.println("Erro total apos " + vezesSomadas + " somas: Ea = "
                + Formatador.formatar(ultimo.erroAbsoluto())
                + " | Er = " + Formatador.formatar(ultimo.erroRelativo()));
        }
        
        System.out.println("");
            
    }
    
     /* Mostra o menu em laço até o usuário escolher "Sair". */
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
