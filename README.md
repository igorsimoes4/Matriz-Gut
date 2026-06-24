# Matriz GUT — Java + JavaFX + SQLite

Aplicação desktop para priorizar problemas usando a Matriz GUT
(**G**ravidade × **U**rgência × **T**endência), com interface gráfica em
JavaFX e persistência em banco SQLite local.

## O que a matriz GUT calcula

Cada problema recebe três notas de 1 a 5:

- **Gravidade**: o quão grave é o impacto se nada for feito.
- **Urgência**: quanto tempo se tem para resolver.
- **Tendência**: tendência do problema piorar com o tempo.

```
Prioridade = Gravidade x Urgência x Tendência   (varia de 1 a 125)
```

Classificação usada no app:

| Pontuação | Classificação |
|-----------|----------------|
| 81 – 125  | Crítica        |
| 41 – 80   | Alta           |
| 15 – 40   | Média          |
| 1 – 14    | Baixa          |

(Você pode ajustar essas faixas em `Problema.getClassificacao()`.)

## Estrutura do projeto

```
gut-matriz/
├── pom.xml
└── src/main/java/com/gutmatriz/
    ├── MainApp.java                       # Tela JavaFX (depende só de interfaces)
    ├── model/
    │   ├── Avaliavel.java                 # interface: contrato "pode ser avaliado por GUT"
    │   ├── ClassificadorPrioridade.java   # interface: estratégia de classificação
    │   ├── ClassificadorPadrao.java       # implementação padrão da estratégia
    │   └── Problema.java                  # entidade (implements Avaliavel)
    ├── db/
    │   └── DatabaseManager.java           # Conexão SQLite + criação da tabela
    └── dao/
        ├── ProblemaRepository.java        # interface: contrato de persistência
        └── ProblemaRepositorySQLite.java  # implementação concreta (SQLite)
```

## Conceitos de POO aplicados

| Conceito | Onde aparece |
|---|---|
| **Encapsulamento** | `Problema` só expõe os atributos via getters/setters; estado interno é privado. |
| **Interface + Polimorfismo** | `MainApp` depende de `ProblemaRepository` e `ClassificadorPrioridade` (tipos abstratos), nunca das classes concretas. Trocar a implementação não exige tocar na tela. |
| **Abstração** | `Avaliavel` define o que significa "ser avaliável pela GUT" sem dizer como cada classe guarda seus dados. |
| **Método default em interface** | `Avaliavel.calcularPrioridade()` já vem pronto (G x U x T) para qualquer classe que implemente a interface. |
| **Strategy pattern** | `ClassificadorPrioridade` permite trocar as regras de classificação (faixas de pontuação) sem alterar `Problema` nem `MainApp`. |
| **Repository pattern** | `ProblemaRepository` isola toda a lógica de banco; `ProblemaRepositorySQLite` é a única classe que sabe SQL. |

**Como estender facilmente graças às interfaces:**
- Quer trocar SQLite por MySQL? Crie `ProblemaRepositoryMySQL implements ProblemaRepository` e troque uma linha em `MainApp`.
- Quer outras faixas de prioridade? Crie `ClassificadorPersonalizado implements ClassificadorPrioridade`.
- Quer avaliar outro tipo de item pela GUT (ex. `Risco`, `TarefaProjeto`)? Basta implementar `Avaliavel`.

## Requisitos

- **JDK 17 ou superior** instalado.
- Maven (o IntelliJ e o Eclipse já trazem o Maven embutido — não precisa instalar nada à parte).
- Conexão com a internet na primeira execução, para o Maven baixar as
  dependências (JavaFX e SQLite JDBC).

## Como abrir

### IntelliJ IDEA
1. `File > Open...` e selecione a pasta `gut-matriz` (a que contém o `pom.xml`).
2. O IntelliJ reconhece automaticamente o projeto Maven e baixa as dependências.

### Eclipse
1. `File > Import... > Maven > Existing Maven Projects`.
2. Aponte para a pasta `gut-matriz` e finalize o import.

## Como rodar

A forma mais simples é pelo terminal (integrado da IDE ou externo), na pasta do projeto:

```bash
mvn clean javafx:run
```

Isso compila e já abre a janela do aplicativo, cuidando de configurar o
módulo do JavaFX automaticamente.

> **Se preferir rodar clicando direto no botão "Run" sobre o `main()`**:
> como o JavaFX não vem embutido no JDK, rodar a classe diretamente
> (sem o plugin Maven) pode gerar o erro *"JavaFX runtime components are
> missing"*. Nesse caso, crie uma Run Configuration que execute o goal
> Maven `javafx:run` em vez de rodar a classe `MainApp` isoladamente.

## Banco de dados

Não é preciso instalar nenhum SGBD. Na primeira execução, o programa cria
automaticamente um arquivo `gut_matrix.db` (SQLite) na raiz do projeto,
com a tabela `problemas`. Os dados ficam salvos entre execuções.

Para resetar tudo, basta apagar o arquivo `gut_matrix.db` e rodar novamente.

## Funcionalidades

- Cadastrar problema com descrição, Gravidade, Urgência e Tendência.
- Lista automaticamente ordenada da maior para a menor prioridade.
- Coluna de classificação colorida (Crítica / Alta / Média / Baixa).
- Editar item selecionado (carrega os dados de volta no formulário).
- Excluir item selecionado (com confirmação).
- Atualizar lista manualmente.
