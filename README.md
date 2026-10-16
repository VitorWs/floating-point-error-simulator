# Simulador de Propagação de Erros Numéricos

Simulador em Java de uma calculadora de **precisão finita**: realiza as quatro operações aritméticas básicas mantendo apenas *n* dígitos significativos na mantissa, por **truncamento** ou **arredondamento simétrico**, e apresenta para cada cálculo o valor exato, o valor aproximado, o erro absoluto ($E_a$) e o erro relativo ($E_r$).

> **Contexto Acadêmico:** Projeto desenvolvido para a disciplina de **Cálculo Numérico** da **UNIVASF** (Fundação Universidade Federal do Vale do São Francisco) — Projeto Unidade 1 (Erros), Semestre 2026.2, Atividade Prática 1.

---

## Sumário

1. [Objetivos do Projeto](#objetivos-do-projeto)
2. [Funcionalidades](#funcionalidades)
3. [Fundamentação e Como o Cálculo é Feito](#fundamentação-e-como-o-cálculo-é-feito)
4. [Requisitos e Como Executar](#requisitos-e-como-executar)
5. [Arquitetura e Estrutura do Projeto](#arquitetura-e-estrutura-do-projeto)
6. [Exemplos de Validação e Testes (t = 4)](#exemplos-de-validação-e-testes-t--4)
7. [Limitações Conhecidas](#limitações-conhecidas)
8. [Equipe e Divisão de Responsabilidades](#equipe-e-divisão-de-responsabilidades)

---

## Objetivos do Projeto

- **Compreender e diferenciar** na prática os conceitos de erro de truncamento e erro de arredondamento simétrico.
- **Implementar** as quatro operações aritméticas básicas (`+`, `-`, `*`, `/`) para números em ponto flutuante normalizados com precisão controlada (*n* dígitos significativos).
- **Analisar** como a precisão finita de um sistema computacional afeta o resultado de cálculos sequenciais (propagação e acúmulo de erros).
- **Calcular e interpretar** o Erro Absoluto ($E_a$) e o Erro Relativo ($E_r$) em diferentes cenários.
- **Observar** o fenômeno crítico do **cancelamento subtrativo** na subtração de números muito próximos.

---

## Funcionalidades

- **Operação única (`x op y`):** calcula `x (+, −, ×, ÷) y` com *n* dígitos significativos e compara, lado a lado em uma única tabela, o resultado obtido por **truncamento** e por **arredondamento simétrico**. Também exibe como os operandos `x` e `y` ficaram armazenados na máquina após o corte inicial, evidenciando visualmente a origem das divergências entre os métodos.
- **Soma sucessiva:** soma um mesmo valor repetidas vezes (ex.: 10 somas seguidas), aplicando o corte de *n* dígitos ao resultado acumulado a cada passo intermediário. Exibe a tabela completa da evolução do erro passo a passo e destaca o erro total acumulado ao final.
- **Duas interfaces de usuário independentes:**
  - **Interface Gráfica (GUI em Java Swing):** janela organizada em abas interativas (*Operação única* e *Soma sucessiva*), com tabelas formatadas e atalhos de teclado.
  - **Interface de Linha de Comando (CLI):** menu interativo no terminal com tabelas alinhadas em texto.
  - Ambas as interfaces compartilham exatamente o mesmo motor matemático (pacote `nucleo`) e permitem realizar múltiplos cálculos seguidos sem precisar reiniciar o programa.
- **Validação robusta de entradas:** entradas não numéricas, campos vazios, quantidades inválidas de dígitos e tentativas de divisão por zero são tratadas com mensagens claras ao usuário, sem interromper ou travar a execução. Os números de entrada aceitam tanto **vírgula** (`0,76545`) quanto **ponto** (`0.76545`) como separador decimal.

---

## Fundamentação e Como o Cálculo é Feito

1. **Normalização em Notação Científica:**
   Todo número real não nulo recebido pelo simulador é convertido para a forma normalizada na base 10:
   $$\pm 0,d_1 d_2 \dots d_n \times 10^e \quad (d_1 \neq 0)$$
   Por exemplo, o número `123,45` é normalizado como `+0,12345 × 10³`.

2. **Corte da Mantissa em *n* Dígitos Significativos:**
   Para simular uma máquina que armazena apenas *n* dígitos na mantissa, aplica-se uma das duas estratégias:
   - **Truncamento:** descarta todos os dígitos após o *n*-ésimo dígito (ex.: `0,76545` com $n = 4$ torna-se `+0,7654 × 10⁰`).
   - **Arredondamento simétrico:** inspeciona o $(n+1)$-ésimo dígito; se for maior ou igual a `5`, soma `1` ao *n*-ésimo dígito, propagando o "vai-um" (*carry*) caso haja dígitos `9` consecutivos (ex.: `0,99996` com $n = 4$ é renormalizado para `+0,1000 × 10¹`).

3. **Aritmética de Precisão Finita:**
   A operação escolhida é executada de forma exata sobre os operandos `x` e `y` **já cortados** em *n* dígitos, e o resultado da operação passa novamente pelo processo de normalização e corte para *n* dígitos — reproduzindo fielmente o ciclo de armazenamento e execução de um processador real.

4. **Referência Exata sem Ruído Binário (`BigDecimal`):**
   O **valor exato** de referência e os erros são calculados internamente com `BigDecimal` (em base 10) a partir dos textos originais digitados pelo usuário. Isso garante que o erro medido provenha exclusivamente do corte em *n* dígitos da máquina hipotética, eliminando distorções de representação binária do tipo `double` (IEEE 754).

5. **Cálculo dos Erros:**
   - **Erro Absoluto:**
     $$E_a = |\text{Valor Exato} - \text{Valor Aproximado}|$$
   - **Erro Relativo:**
     $$E_r = \frac{|\text{Valor Exato} - \text{Valor Aproximado}|}{|\text{Valor Aproximado}|}$$
     *(Exibido como **Indefinido** quando o valor aproximado é zero).*

---

## Requisitos e Como Executar

### Requisitos
- **Java JDK 17 ou superior** (utiliza recursos modernos como `record` e `switch` expressions).

### 1. Executando pelo Terminal (Compilação Manual)

Na pasta raiz do projeto, compile todos os arquivos `.java`:

```bash
# Linux / macOS
javac -encoding UTF-8 -d out $(find src -name "*.java")
```

```powershell
# Windows (PowerShell)
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

Em seguida, escolha qual interface deseja iniciar:

```bash
# Iniciar a Janela Gráfica (Swing)
java -cp out gui.Janela

# Iniciar a Interface de Linha de Comando (CLI)
java -cp out commandLineInteface.Cli

# Executar a bateria rápida de testes de validação do núcleo
java -cp out nucleo.DemoNucleo
```

### 2. Gerando e Executando um Arquivo `.jar`

Após compilar na pasta `out`, você pode empacotar o projeto em um JAR executável:

```bash
jar cfe simulador.jar gui.Janela -C out .

# Executa a janela gráfica (ou dê duplo clique no arquivo simulador.jar)
java -jar simulador.jar

# Executa a linha de comando a partir do JAR
java -cp simulador.jar commandLineInteface.Cli
```

### 3. Executando pelo NetBeans ou VS Code

1. Abra a pasta do projeto na sua IDE de preferência (no NetBeans, crie um projeto Java a partir de código-fonte existente apontando para a pasta `src/`).
2. Execute a classe `gui/Janela.java` para abrir a interface gráfica ou `commandLineInteface/Cli.java` para rodar no console.

---

## Arquitetura e Estrutura do Projeto

O código-fonte foi projetado seguindo o princípio de **separação de responsabilidades**, isolando completamente a lógica matemática das interfaces de usuário:

```text
src/
├── nucleo/                        # Motor matemático (não faz leitura nem impressão na tela)
│   ├── NumeroPontoFlutuante.java  # Representação ±0,d1...dn × 10^e, normalização, corte e operações
│   ├── ModoCorte.java             # Enum dos métodos de corte: TRUNCAMENTO e ARREDONDAMENTO
│   ├── Operacao.java              # Enum das operações aritméticas: SOMA, SUBTRACAO, MULTIPLICACAO, DIVISAO
│   ├── Erros.java                 # Cálculo de Erro Absoluto (Ea) e Erro Relativo (Er) com BigDecimal
│   ├── ResultadoOperacao.java     # Record imutável com operandos cortados, resultados e erros
│   ├── Simulador.java             # Fachada (Facade) do núcleo para operação única e soma sucessiva
│   └── DemoNucleo.java            # Script de verificação rápida dos casos de teste do edital
├── apresentacao/                  # Camada de formatação textual
│   └── Formatador.java            # Formata valores em pt-BR (vírgula decimal, 6 dígitos significativos)
├── commandLineInteface/           # Interface de Linha de Comando (Terminal)
│   └── Cli.java                   # Menu interativo em laço, validação de entradas e tabelas em texto
└── gui/                           # Interface Gráfica em Java Swing
    ├── Janela.java                # JFrame principal com navegação por abas (JTabbedPane)
    ├── PainelOperacao.java        # Aba de "Operação única" (comparação lado a lado e operandos cortados)
    └── PainelSoma.java            # Aba de "Soma sucessiva" (tabelas passo a passo por modo de corte)
```

---

## Exemplos de Validação e Testes ($t = 4$)

A tabela abaixo resume os resultados obtidos pelo simulador para os casos clássicos do projeto, considerando uma máquina hipotética com $t = 4$ dígitos significativos:

| Caso de Teste | Entrada | Modo de Corte | Resultado Aproximado | Erro Absoluto ($E_a$) | Erro Relativo ($E_r$) |
|---|---|---|---|---|---|
| **1. Operação Simples (Soma)** | `0,34567 + 0,45678`<br>*(Exato: `0,80245`)* | Truncamento<br>Arredondamento | `0,8023` (`+0,8023 × 10⁰`)<br>`0,8025` (`+0,8025 × 10⁰`) | `0,00015`<br>`0,00005` | `0,000186962` (~0,019%)<br>`0,0000623053` (~0,006%) |
| **2. Cancelamento Subtrativo** | `0,76545 − 0,76541`<br>*(Exato: `0,00004`)* | Truncamento<br>Arredondamento | `0` (`+0,0000 × 10⁰`)<br>`0,0001` (`+0,1000 × 10⁻³`) | `0,00004`<br>`0,00006` | `Indefinido` (divisão por 0)<br>`0,6` (**60%**) |
| **3. Propagação em 10 Somas** | `0,56786` somado 10x<br>*(Exato: `5,6786`)* | Truncamento<br>Arredondamento | `5,671` (`+0,5671 × 10¹`)<br>`5,680` (`+0,5680 × 10¹`) | `0,0076`<br>`0,0014` | `0,00134015` (~0,134%)<br>`0,000246479` (~0,025%) |

---

## Limitações Conhecidas

- **Conversão para exibição:** Os valores finais são convertidos para `double` apenas no momento da formatação visual para as tabelas. Números com expoentes extremos fora do alcance do `double` IEEE 754 (ex.: `1e400` ou `1e-400`) são exibidos como `Infinity` ou `0`, embora o cálculo interno em `BigDecimal` permaneça íntegro.
- **Precisão interna na divisão:** A divisão em precisão finita utiliza 60 dígitos significativos intermediários antes do corte, e o valor exato de referência utiliza `MathContext.DECIMAL128` (34 dígitos). Na interface gráfica, *n* é limitado a 50 dígitos e a soma sucessiva a 1000 passos.

---

## Equipe e Divisão de Responsabilidades

Projeto desenvolvido em equipe com a seguinte divisão de atribuições:

| Integrante | Responsabilidade Principal | Atividades Desenvolvidas |
|---|---|---|
| **Vitor Santos Pereira de Souza** | **Núcleo de Cálculo (`nucleo`)** | Arquitetura e implementação do motor matemático do simulador: normalização em notação científica ($\pm 0,d_1\dots d_n \times 10^e$), algoritmos de truncamento e arredondamento simétrico com propagação de "vai-um", operações aritméticas em precisão finita, cálculo exato de erros ($E_a$ e $E_r$) com `BigDecimal` e fachada `Simulador`. |
| **Laedson Araújo Silva Júnior** | **Interfaces e Páginas (`gui`, `commandLineInteface`, `apresentacao`)** | Desenvolvimento completo das telas da interface gráfica em Java Swing (`Janela`, `PainelOperacao` e `PainelSoma`), construção da interface de linha de comando interativa (`Cli`), validação de entradas do usuário e formatação visual das tabelas e números (`Formatador`). |
| **Myguel Luiz Quixaba Alves** | **Relatório Técnico e Análise de Resultados** | Elaboração do relatório técnico-científico, execução sistemática da bateria de testes para validação ($t = 4$), organização das tabelas comparativas e análise crítica dos fenômenos numéricos observados (viés do truncamento, cancelamento subtrativo, número de condição e acúmulo de erros). |
