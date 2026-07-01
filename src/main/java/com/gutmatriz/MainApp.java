package com.gutmatriz;

import com.gutmatriz.dao.ProblemaRepository;
import com.gutmatriz.dao.ProblemaRepositorySQLite;
import com.gutmatriz.db.InicializadorBanco;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

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

        InicializadorBanco.inicializar();
        carregarDados();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f6f9;");

        VBox topo = new VBox(15,
                criarCabecalho(),
                criarFormulario()
        );

        root.setTop(topo);
        root.setCenter(criarTabela());
        root.setBottom(criarBarraInferior());

        BorderPane.setMargin(root.getCenter(), new Insets(15, 0, 15, 0));

        Scene scene = new Scene(root, 1200, 750);

        stage.setTitle("Matriz GUT - Sistema de Priorização");
        stage.setScene(scene);
        stage.show();
    }

    private HBox criarCabecalho() {

        Label titulo = new Label("📊 MATRIZ GUT");
        titulo.setStyle("""
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                -fx-text-fill: #2c3e50;
                """);

        Label subtitulo = new Label("Sistema de Priorização de Problemas");
        subtitulo.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #7f8c8d;
                """);

        VBox textos = new VBox(5, titulo, subtitulo);

        HBox cabecalho = new HBox(textos);
        cabecalho.setPadding(new Insets(20));

        cabecalho.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #dfe6e9;
                """);

        return cabecalho;
    }

    private GridPane criarFormulario() {

        GridPane grid = new GridPane();

        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(12);

        grid.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #dfe6e9;
                """);

        Label tituloDescricao = new Label("Descrição do Problema");

        campoDescricao = new TextField();
        campoDescricao.setPromptText("Ex: Servidor indisponível em horários de pico");
        campoDescricao.setPrefWidth(500);

        Label tituloG = new Label("Gravidade");
        comboGravidade = criarComboNotas();

        Label tituloU = new Label("Urgência");
        comboUrgencia = criarComboNotas();

        Label tituloT = new Label("Tendência");
        comboTendencia = criarComboNotas();

        botaoAcao = new Button("Adicionar");
        botaoAcao.setStyle("""
                -fx-background-color: #3498db;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                """);

        botaoAcao.setOnAction(e -> salvar());

        Button botaoLimpar = new Button("Limpar");
        botaoLimpar.setOnAction(e -> limparFormulario());

        grid.add(tituloDescricao, 0, 0);
        grid.add(campoDescricao, 1, 0, 5, 1);

        grid.add(tituloG, 0, 1);
        grid.add(comboGravidade, 1, 1);

        grid.add(tituloU, 2, 1);
        grid.add(comboUrgencia, 3, 1);

        grid.add(tituloT, 4, 1);
        grid.add(comboTendencia, 5, 1);

        HBox botoes = new HBox(10, botaoAcao, botaoLimpar);
        botoes.setAlignment(Pos.CENTER_RIGHT);

        grid.add(botoes, 5, 2);

        return grid;
    }

    private ComboBox<Integer> criarComboNotas() {

        ComboBox<Integer> combo = new ComboBox<>(
                FXCollections.observableArrayList(1, 2, 3, 4, 5)
        );

        combo.setValue(1);

        return combo;
    }

    private TableView<Problema> criarTabela() {

        tabela = new TableView<>();

        tabela.setItems(dados);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tabela.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #dfe6e9;
                """);

        TableColumn<Problema, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Problema, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Problema, Integer> colG = new TableColumn<>("G");
        colG.setCellValueFactory(new PropertyValueFactory<>("gravidade"));

        TableColumn<Problema, Integer> colU = new TableColumn<>("U");
        colU.setCellValueFactory(new PropertyValueFactory<>("urgencia"));

        TableColumn<Problema, Integer> colT = new TableColumn<>("T");
        colT.setCellValueFactory(new PropertyValueFactory<>("tendencia"));

        TableColumn<Problema, Number> colPrioridade =
                new TableColumn<>("Prioridade");

        colPrioridade.setCellValueFactory(cell ->
                new SimpleIntegerProperty(
                        cell.getValue().calcularPrioridade()
                ));

        colPrioridade.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(Number item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                int valor = item.intValue();

                setText(String.valueOf(valor));

                setStyle("-fx-font-weight:bold;");

                if (valor >= 80)
                    setTextFill(Color.RED);
                else if (valor >= 40)
                    setTextFill(Color.ORANGE);
                else if (valor >= 20)
                    setTextFill(Color.GOLDENROD);
                else
                    setTextFill(Color.GREEN);
            }
        });

        TableColumn<Problema, String> colClassificacao =
                new TableColumn<>("Classificação");

        colClassificacao.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        classificador.classificar(
                                cell.getValue().calcularPrioridade()
                        )
                ));

        colClassificacao.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(item);
                setStyle("-fx-font-weight:bold;");

                switch (item) {

                    case "Crítica" ->
                            setTextFill(Color.web("#c0392b"));

                    case "Alta" ->
                            setTextFill(Color.web("#e67e22"));

                    case "Média" ->
                            setTextFill(Color.web("#f1c40f"));

                    default ->
                            setTextFill(Color.web("#27ae60"));
                }
            }
        });

        TableColumn<Problema, String> colData =
                new TableColumn<>("Criado em");

        colData.setCellValueFactory(
                new PropertyValueFactory<>("dataCriacao")
        );

        tabela.getColumns().addAll(List.of(
                colId,
                colDescricao,
                colG,
                colU,
                colT,
                colPrioridade,
                colClassificacao,
                colData
        ));

        return tabela;
    }

    private HBox criarBarraInferior() {

        Button botaoEditar = new Button("✏ Editar");
        botaoEditar.setOnAction(e -> carregarParaEdicao());

        Button botaoExcluir = new Button("🗑 Excluir");
        botaoExcluir.setOnAction(e -> excluirSelecionado());

        Button botaoAtualizar = new Button("🔄 Atualizar");
        botaoAtualizar.setOnAction(e -> carregarDados());

        labelStatus = new Label();

        HBox box = new HBox(
                15,
                botaoEditar,
                botaoExcluir,
                botaoAtualizar,
                labelStatus
        );

        box.setPadding(new Insets(15));

        box.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #dfe6e9;
                """);

        box.setAlignment(Pos.CENTER_LEFT);

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

            Problema novo = new Problema(
                    0,
                    descricao.trim(),
                    g,
                    u,
                    t,
                    ""
            );

            repositorio.inserir(novo);

            labelStatus.setText("Problema adicionado com sucesso.");

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

        Problema selecionado =
                tabela.getSelectionModel().getSelectedItem();

        if (selecionado == null) {

            labelStatus.setText(
                    "Selecione um problema para editar."
            );

            return;
        }

        problemaEmEdicao = selecionado;

        campoDescricao.setText(
                selecionado.getDescricao()
        );

        comboGravidade.setValue(
                selecionado.getGravidade()
        );

        comboUrgencia.setValue(
                selecionado.getUrgencia()
        );

        comboTendencia.setValue(
                selecionado.getTendencia()
        );

        botaoAcao.setText("Salvar Alteração");

        labelStatus.setText(
                "Editando item #" + selecionado.getId()
        );
    }

    private void excluirSelecionado() {

        Problema selecionado =
                tabela.getSelectionModel().getSelectedItem();

        if (selecionado == null) {

            labelStatus.setText(
                    "Selecione um item para excluir."
            );

            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deseja realmente excluir este problema?",
                ButtonType.YES,
                ButtonType.NO
        );

        confirmacao.showAndWait().ifPresent(resposta -> {

            if (resposta == ButtonType.YES) {

                repositorio.excluir(
                        selecionado.getId()
                );

                carregarDados();

                labelStatus.setText(
                        "Problema removido."
                );
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