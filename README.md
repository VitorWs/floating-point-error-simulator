# Simulador de Propagação de Erros Numéricos
 
Simulador em Java de uma calculadora de **precisão finita**: realiza as quatro operações aritméticas mantendo apenas *n* dígitos significativos, por **truncamento** ou **arredondamento simétrico**, e mostra o valor exato, o valor aproximado, o erro absoluto e o erro relativo de cada cálculo.
 
Projeto da disciplina de Cálculo Numérico (UNIVASF), Projeto Unidade 1 (Erros), 2026.2, Atividade Prática 1.
 
## Funcionalidades
 
- **Operação única:** calcula `x (+, −, ×, ÷) y` com *n* dígitos significativos e compara, lado a lado, o resultado por truncamento e por arredondamento. Também mostra os operandos como ficaram armazenados depois do corte, o que explica por que os dois modos divergem.
- **Soma sucessiva:** soma o mesmo valor várias vezes, cortando o acumulado a cada passo, e mostra a evolução do erro passo a passo, com o erro total no final.
- **Duas interfaces:** janela gráfica (Swing) e linha de comando, as duas usando o mesmo núcleo de cálculo. Em ambas é possível fazer novos cálculos sem reiniciar o programa.
- **Validação das entradas:** valores inválidos e divisão por zero são avisados ao usuário, sem encerrar o programa. Os números aceitam vírgula ou ponto como separador decimal.
## Como o cálculo é feito
 
1. Os operandos `x` e `y` são **normalizados** em notação científica, `±0,d₁d₂…dₙ × 10ᵉ` (por exemplo, `123,45 → 0,12345 × 10³`), e cortados em *n* dígitos significativos.
2. A operação é feita com os operandos já cortados, e o resultado é **normalizado e cortado de novo** em *n* dígitos, como numa máquina de precisão finita.
3. O **valor exato** é calculado com as entradas originais, usando `BigDecimal` (a precisão máxima do Java), para que o erro medido venha só do corte em *n* dígitos, e não das limitações do tipo `double`.
4. Os erros são calculados como:
   - Erro absoluto: `Ea = |valor exato − valor aproximado|`
   - Erro relativo: `Er = Ea / |valor aproximado|`, exibido como **Indefinido** quando o valor aproximado é zero.
**Modos de corte:**
 
- **Truncamento:** descarta os dígitos depois do *n*-ésimo.
- **Arredondamento simétrico:** soma 1 ao *n*-ésimo dígito quando o dígito seguinte é 5 ou mais, propagando o "vai-um" (por exemplo, `0,99996 → 0,1000 × 10¹` com *n* = 4).
O programa calcula sempre os **dois modos ao mesmo tempo**, para que o resultado de truncamento e o de arredondamento possam ser comparados numa única tabela, como pede o procedimento de testes.
 
## Requisitos
 
- **Java 17 ou superior** (o código usa `record` e `switch` com setas).
## Como executar
 
### Pelo terminal
 
Na pasta raiz do projeto, compile:
 
```
# Linux / macOS
javac -encoding UTF-8 -d out $(find src -name "*.java")
 
# Windows (PowerShell)
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```
 
Depois execute:
 
```
java -cp out gui.Janela                  # janela gráfica
java -cp out commandLineInteface.Cli     # linha de comando
```
 
Para gerar um JAR executável (abre a janela com duplo clique ou com `java -jar`):
 
```
jar cfe simulador.jar gui.Janela -C out .
java -jar simulador.jar                           # janela gráfica
java -cp simulador.jar commandLineInteface.Cli    # linha de comando
```
 
### Pelo NetBeans ou VS Code
 
1. Abra a pasta do projeto (no NetBeans, crie um projeto Java a partir de código existente, apontando para a pasta `src`).
2. Execute `gui/Janela.java` (janela gráfica) ou `commandLineInteface/Cli.java` (linha de comando).
## Estrutura do projeto
 
```
src/
├── nucleo/                Cálculo (não lê nem imprime nada)
│   ├── NumeroPontoFlutuante   número normalizado com n dígitos, corte e operações
│   ├── ModoCorte              truncamento ou arredondamento simétrico
│   ├── Operacao               +, −, ×, ÷
│   ├── Erros                  erro absoluto e erro relativo
│   ├── ResultadoOperacao      resultado de uma operação, com os erros
│   ├── Simulador              ponto de entrada do núcleo para as interfaces
│   └── DemoNucleo             teste rápido do núcleo com os exemplos do enunciado
├── apresentacao/          Formatador: converte números em texto para exibição
├── commandLineInteface/   Cli: interface de linha de comando
└── gui/                   Janela, PainelOperacao e PainelSoma: interface gráfica
```
 
Como o núcleo não faz entrada nem saída, o mesmo cálculo é usado pela linha de comando e pela janela.
 
## Exemplos de teste (t = 4)
 
| Exemplo | Entrada | Truncamento | Arredondamento |
|---|---|---|---|
| 1. Soma simples | `0,34567 + 0,45678` (exato: 0,80245) | 0,8023 (Ea = 0,00015) | 0,8025 (Ea = 0,00005) |
| 2. Cancelamento subtrativo | `0,76545 − 0,76541` (exato: 0,00004) | 0 (Ea = 0,00004; Er indefinido) | 0,0001 (Ea = 0,00006; Er = 0,6) |
| 3. Soma sucessiva | `0,56786` somado 10 vezes (exato: 5,6786) | 5,671 (Ea = 0,0076) | 5,680 (Ea = 0,0014) |
 
## Limitações conhecidas
 
- Os valores são convertidos para `double` apenas na exibição. Números fora do alcance do `double` (por exemplo, `1e400` ou `1e-400`) aparecem como `Infinity` ou `0` na tela, embora o cálculo interno seja feito corretamente.
- Na linha de comando não há limite máximo para *n* nem para a quantidade de somas. Na janela, os limites são 50 dígitos e 1000 somas.
## Equipe
 
- Laedson Araújo Silva Júnior
- Myguel Luiz Quixaba Alves
- Vitor Santos Pereira de Souza
