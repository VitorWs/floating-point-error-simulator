# floating-point-error-simulator

Simulador em Java para análise de aritmética de precisão finita, propagação de erros, truncamento, arredondamento simétrico e cancelamento subtrativo.

Projeto desenvolvido no âmbito da unidade curricular de Cálculo Numérico. Através da parametrização de *n* dígitos significativos na mantissa, a ferramenta permite avaliar os métodos de corte (truncamento e arredondamento simétrico), monitorizar a propagação e acumulação de erros numéricos e evidenciar fenómenos críticos de instabilidade matemática, tais como o cancelamento subtrativo.

## Funcionalidades

- **Operação única:** calcula `x (+, -, *, /) y` com *n* dígitos significativos, por truncamento e por arredondamento, e compara os dois resultados lado a lado (valor exato, valor aproximado, erro absoluto e erro relativo).
- **Soma sucessiva:** soma um mesmo valor várias vezes, cortando o acumulado a cada passo, e mostra a evolução do erro passo a passo, com o erro total no final.
- **Duas interfaces:** janela gráfica (Swing) e linha de comando (CLI), ambas usando o mesmo núcleo de cálculo.

Os erros são calculados como:

- Erro absoluto: `Ea = |valor exato − valor aproximado|`
- Erro relativo: `Er = Ea / |valor aproximado|`, exibido como *Indefinido* quando o valor aproximado é zero.

## Requisitos

- **Java 17 ou superior** (o código usa `record` e `switch` com setas).

## Como executar

### Pelo JAR (mais simples)

```
java -jar simulador.jar
```

Abre a janela gráfica. Para a versão em linha de comando:

```
java -cp simulador.jar userInterface.Cli
```

### Pelo NetBeans

1. Abra o projeto.
2. Clique com o botão direito em `gui/Janela.java` e escolha *Run File* (janela gráfica), ou em `userInterface/Cli.java` (linha de comando).
3. Para gerar o JAR, use *Clean and Build* (o arquivo fica em `dist/`).

### Pelo terminal (compilando)

Na pasta raiz do projeto:

```
# Linux / macOS
javac -encoding UTF-8 -d out $(find src -name "*.java")

# Windows (PowerShell)
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

Depois:

```
java -cp out gui.Janela            # janela gráfica
java -cp out userInterface.Cli     # linha de comando
```

## Estrutura do projeto

```
src/
├── nucleo/          Cálculo: número de ponto flutuante normalizado, operações,
│                    modos de corte, erros e o Simulador (fachada do núcleo)
├── apresentacao/    Formatador: converte números em texto para exibição
├── userInterface/   Cli: interface de linha de comando
└── gui/             Janela, PainelOperacao e PainelSoma: interface gráfica
```

O pacote `nucleo` não lê nem imprime nada: toda a entrada e saída fica nas interfaces, o que permite usar o mesmo cálculo na CLI e na janela.

## Exemplos de teste (t = 4)

| Teste | Entrada | Resultado esperado |
|---|---|---|
| Soma simples | `0,34567 + 0,45678` | Truncamento: 0,8023 (Ea = 0,00015). Arredondamento: 0,8025 (Ea = 0,00005) |
| Cancelamento subtrativo | `0,76545 − 0,76541` | Arredondamento: 0,0001 (Ea = 0,00006, Er = 0,6). Truncamento: 0 (Er indefinido) |
| Dez somas | `0,56786` somado 10 vezes | Truncamento: 5,671 (Ea = 0,0076). Arredondamento: 5,680 (Ea = 0,0014) |

## Equipe

Laedson Araújo Silva Júnior

Myguel Luiz Quixaba Alves

Vitor Santos Pereira de Souza
