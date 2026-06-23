package com.gutmatriz;

import com.gutmatriz.dao.ProblemaRepository;
import com.gutmatriz.dao.ProblemaRepositorySQLite;
import com.gutmatriz.db.DatabaseManager;
import com.gutmatriz.model.ClassificadorPadrao;
import com.gutmatriz.model.ClassificadorPrioridade;
import com.gutmatriz.model.Problema;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    // Dependências por interface: a tela não sabe (nem precisa saber)
    // que por trás existe SQLite e faixas fixas de classificação.
    private final ProblemaRepository repositorio = new ProblemaRepositorySQLite();
    private final ClassificadorPrioridade classificador = new ClassificadorPadrao();

    private final ObservableList<Problema> dados = FXCollections.observableArrayList();

    private TableView<Problema> tabela;
    private TextField campoDescricao;
    private ComboBox<Integer> comboGravidade;
    private ComboBox<Integer> comboUrgencia;
    private ComboBox<Integer> comboTendencia;
    private Button botaoAcao;
    private Label labelStatus;

    private Problema problemaEmEdicao = null;

    @Override
    public void start(Stage stage) {
        DatabaseManager.inicializarBanco();
        carregarDados();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setTop(criarFormulario());
        root.setCenter(criarTabela());
        root.setBottom(criarBarraInferior());
        BorderPane.setMargin(root.getCenter(), new Insets(15, 0, 15, 0));

        Scene scene = new Scene(root, 950, 600);
        stage.setTitle("Matriz GUT — Priorização de Problemas");
        stage.setScene(scene);
        stage.show();
    }

    private GridPane criarFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        Label tituloDescricao = new Label("Descrição do problema:");
        campoDescricao = new TextField();
        campoDescricao.setPromptText("Ex: Servidor caindo nos horários de pico");
        campoDescricao.setPrefWidth(420);

        Label tituloG = new Label("Gravidade (1-5):");
        comboGravidade = criarComboNotas();
        Label tituloU = new Label("Urgência (1-5):");
        comboUrgencia = criarComboNotas();
        Label tituloT = new Label("Tendência (1-5):");
        comboTendencia = criarComboNotas();

        botaoAcao = new Button("Adicionar");
        botaoAcao.setDefaultButton(true);
        botaoAcao.setOnAction(e -> salvar());

        Button botaoLimpar = new Button("Limpar");
        botaoLimpar.setOnAction(e -> limparFormulario());

        grid.add(tituloDescricao, 0, 0);
        grid.add(campoDescricao, 1, 0, 3, 1);

        grid.add(tituloG, 0, 1);
        grid.add(comboGravidade, 1, 1);
        grid.add(tituloU, 2, 1);
        grid.add(comboUrgencia, 3, 1);

        HBox linhaTendencia = new HBox(10, tituloT, comboTendencia, botaoAcao, botaoLimpar);
        linhaTendencia.setAlignment(Pos.CENTER_LEFT);
        grid.add(linhaTendencia, 0, 2, 4, 1);

        return grid;
    }

    private ComboBox<Integer> criarComboNotas() {
        ComboBox<Integer> combo = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        combo.setValue(1);
        return combo;
    }

    private TableView<Problema> criarTabela() {
        tabela = new TableView<>();
        tabela.setItems(dados);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Problema, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(50);

        TableColumn<Problema, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Problema, Integer> colG = new TableColumn<>("G");
        colG.setCellValueFactory(new PropertyValueFactory<>("gravidade"));
        colG.setMaxWidth(45);

        TableColumn<Problema, Integer> colU = new TableColumn<>("U");
        colU.setCellValueFactory(new PropertyValueFactory<>("urgencia"));
        colU.setMaxWidth(45);

        TableColumn<Problema, Integer> colT = new TableColumn<>("T");
        colT.setCellValueFactory(new PropertyValueFactory<>("tendencia"));
        colT.setMaxWidth(45);

        // calcularPrioridade() vem da interface Avaliavel (método default)
        TableColumn<Problema, Number> colPrioridade = new TableColumn<>("Prioridade");
        colPrioridade.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().calcularPrioridade()));
        colPrioridade.setMaxWidth(90);

        // classificar(...) vem da estratégia injetada (ClassificadorPrioridade)
        TableColumn<Problema, String> colClassificacao = new TableColumn<>("Classificação");
        colClassificacao.setCellValueFactory(cell ->
                new SimpleStringProperty(classificador.classificar(cell.getValue().calcularPrioridade())));
        colClassificacao.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                setStyle("-fx-font-weight: bold;");
                switch (item) {
                    case "Crítica" -> setTextFill(Color.web("#c0392b"));
                    case "Alta" -> setTextFill(Color.web("#e67e22"));
                    case "Média" -> setTextFill(Color.web("#d4ac0d"));
                    default -> setTextFill(Color.web("#27ae60"));
                }
            }
        });

        TableColumn<Problema, String> colData = new TableColumn<>("Criado em");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));

        tabela.getColumns().addAll(List.of(
                colId, colDescricao, colG, colU, colT, colPrioridade, colClassificacao, colData));

        return tabela;
    }

    private HBox criarBarraInferior() {
        Button botaoEditar = new Button("Editar selecionado");
        botaoEditar.setOnAction(e -> carregarParaEdicao());

        Button botaoExcluir = new Button("Excluir selecionado");
        botaoExcluir.setOnAction(e -> excluirSelecionado());

        Button botaoAtualizar = new Button("Atualizar lista");
        botaoAtualizar.setOnAction(e -> carregarDados());

        labelStatus = new Label("");

        HBox box = new HBox(10, botaoEditar, botaoExcluir, botaoAtualizar, labelStatus);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10, 0, 0, 0));
        return box;
    }

    private void salvar() {
        String descricao = campoDescricao.getText();
        if (descricao == null || descricao.isBlank()) {
            labelStatus.setText("Informe a descrição do problema.");
            return;
        }

        int g = comboGravidade.getValue();
        int u = comboUrgencia.getValue();
        int t = comboTendencia.getValue();

        if (problemaEmEdicao == null) {
            Problema novo = new Problema(0, descricao.trim(), g, u, t, "");
            repositorio.inserir(novo);
            labelStatus.setText("Problema adicionado.");
        } else {
            problemaEmEdicao.setDescricao(descricao.trim());
            problemaEmEdicao.setGravidade(g);
            problemaEmEdicao.setUrgencia(u);
            problemaEmEdicao.setTendencia(t);
            repositorio.atualizar(problemaEmEdicao);
            labelStatus.setText("Problema atualizado.");
        }

        limparFormulario();
        carregarDados();
    }

    private void carregarParaEdicao() {
        Problema selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            labelStatus.setText("Selecione um item na tabela para editar.");
            return;
        }
        problemaEmEdicao = selecionado;
        campoDescricao.setText(selecionado.getDescricao());
        comboGravidade.setValue(selecionado.getGravidade());
        comboUrgencia.setValue(selecionado.getUrgencia());
        comboTendencia.setValue(selecionado.getTendencia());
        botaoAcao.setText("Salvar alteração");
        labelStatus.setText("Editando item #" + selecionado.getId());
    }

    private void excluirSelecionado() {
        Problema selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            labelStatus.setText("Selecione um item na tabela para excluir.");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir o problema \"" + selecionado.getDescricao() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText(null);
        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.YES) {
                repositorio.excluir(selecionado.getId());
                carregarDados();
                labelStatus.setText("Problema excluído.");
            }
        });
    }

    private void limparFormulario() {
        campoDescricao.clear();
        comboGravidade.setValue(1);
        comboUrgencia.setValue(1);
        comboTendencia.setValue(1);
        botaoAcao.setText("Adicionar");
        problemaEmEdicao = null;
    }

    private void carregarDados() {
        dados.setAll(repositorio.listarTodos());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
