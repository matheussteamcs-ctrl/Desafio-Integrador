# Sistema de Gestão de Pedidos

Centro Universitário Campo Real — Engenharia de Software — 3° Período
Desafio Integrador 2026

---

## 👥 Integrantes

| Nome | Responsabilidades |
|---|---|
| Gabriel | Models Pedido/ItemPedido, Exceções, ClienteRepository, ClienteService, PedidoRepository, RelatorioRepository, DDL SQL, ConnectionFactory, README, Diagrama do BD |
| Gustavo | Enums, Models Cliente/Produto, ProdutoRepository, ProdutoService, PedidoService, OrderProcessorThread, Relatório 2, Diagrama de Classes, Documento de Requisitos |
| Matheus | Todos os Menus (ClienteMenu, ProdutoMenu, PedidoMenu, RelatorioMenu, MainMenu), Relatório de Funções |

---

## 📁 Estrutura do Projeto

```
Desafio-Integrador/
├── src/
│   ├── enums/
│   │   ├── Categoria.java
│   │   └── StatusPedido.java
│   ├── exception/
│   │   ├── EstoqueInsuficienteException.java
│   │   ├── PedidoNotFoundException.java
│   │   └── ValidacaoException.java
│   ├── model/
│   │   ├── Cliente.java
│   │   ├── ItemPedido.java
│   │   ├── Pedido.java
│   │   └── Produto.java
│   ├── repository/
│   │   ├── ClienteRepository.java
│   │   ├── PedidoRepository.java
│   │   ├── ProdutoRepository.java
│   │   └── RelatorioRepository.java
│   ├── service/
│   │   ├── ClienteService.java
│   │   ├── PedidoService.java
│   │   └── ProdutoService.java
│   ├── thread/
│   │   └── OrderProcessorThread.java
│   ├── ui/
│   │   ├── ClienteMenu.java
│   │   ├── MainMenu.java
│   │   ├── PedidoMenu.java
│   │   ├── ProdutoMenu.java
│   │   └── RelatorioMenu.java
│   └── util/
│       └── ConnectionFactory.java
├── sql/
│   └── schema.sql
├── docs/
│   ├── diagrama-bd.pdf
│   ├── diagrama-classes.pdf
│   └── requisitos.pdf
└── README.md
```

---

## ⚙️ Pré-requisitos

- Java 17 ou superior
- MySQL 8.0 ou superior
- Git


## 🏗️ Decisões Arquiteturais

### 1. Isolamento do SQL no pacote repository/
Todo o código JDBC e SQL foi concentrado exclusivamente no pacote `repository/`.
Nenhuma classe do pacote `ui/` importa `java.sql` — isso garante separação de responsabilidades e facilita manutenção.

**Exemplo correto:**
```java

import service.ClienteService; 
```

**Exemplo proibido:**
```java

import java.sql.Connection; 
```

### 2. Objetos imutáveis — zero setters
Todos os models (`Cliente`, `Produto`, `Pedido`, `ItemPedido`) têm atributos `final` e apenas getters.
Os objetos nascem completos pelo construtor ao ler do ResultSet:
```java

return new Cliente(
    rs.getInt("id"),
    rs.getString("nome"),
    rs.getString("email")
);


cliente.setNome(rs.getString("nome")); 

### 3. PreparedStatement em todas as queries
Todas as queries usam `PreparedStatement` com parâmetros `?`.
Isso previne SQL Injection e melhora a performance.

### 4. try-with-resources em todo acesso ao banco
Garante que `Connection`, `PreparedStatement` e `ResultSet` sejam fechados automaticamente,
mesmo em caso de exceção:
```java
try (Connection conn = ConnectionFactory.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    
}
```

### 5. Transação manual no PedidoRepository
O salvamento de pedidos usa controle transacional manual para garantir atomicidade:
```java
conn.setAutoCommit(false);

conn.commit();   
conn.rollback();
```

### 6. UPDATE condicional de estoque no SQL
O estoque nunca é verificado em Java — a condição fica no próprio SQL:
```sql
UPDATE produto SET estoque = estoque - ? WHERE id = ? AND estoque >= ?
```
Se retornar 0 linhas afetadas: estoque ficaria negativo → rollback + EstoqueInsuficienteException.

### 7. Thread com conexão isolada
A `OrderProcessorThread` abre e fecha sua própria `Connection` a cada ciclo,
sem compartilhar conexão com o menu principal.
Isso evita conflitos de concorrência.

### 8. UPDATE atômico da thread
Para evitar que dois ciclos peguem o mesmo pedido simultaneamente:
```sql
UPDATE pedido SET status = 'PROCESSANDO' WHERE id = ? AND status = 'FILA'
```
Se retornar 0 linhas: outro ciclo já pegou esse pedido — ignora e continua.

---

## 📊 Fluxo de Status do Pedido

```
ABERTO → FILA → PROCESSANDO → FINALIZADO
  ↑         ↑         ↑             ↑
usuário   usuário   thread        thread
monta    confirma   pegou        concluiu
```

---

## 📋 Regras Técnicas Obrigatórias

| Regra | Descrição |
|---|---|
| PreparedStatement | Usado em todas as queries — nunca Statement simples |
| try-with-resources | Em todo acesso ao banco |
| Zero setters | Objetos criados pelo construtor — atributos final |
| SQL isolado | Pacote ui/ nunca importa java.sql |
| Estoque condicional | UPDATE com condição no SQL, nunca em Java |
| Thread isolada | Conexão própria a cada ciclo |