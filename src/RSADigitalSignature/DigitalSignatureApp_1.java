package RSADigitalSignature;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;

public class DigitalSignatureApp_1 extends Application {

    private ComboBox<Integer> keySizeComboBox;
    private TextArea privateKeyArea;
    private TextArea publicKeyArea;
    private TextField senderFileField;
    private TextField receiverFileField;
    private TextArea senderHashArea;
    private TextArea receiverHashArea;
    private TextArea signatureArea;
    private TextField verificationResultField;
    private ProgressBar progressBar;
    
    private final RSAUtils_1 rsa = new RSAUtils_1();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Digital Signature Software - Enhanced");

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // Title section
        HBox titleBox = createTitleBox();
        root.setTop(titleBox);

        // Main content
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(10));
        
        // Key section
        TitledPane keyGroup = createKeyGroup();
        
        // Sender section
        TitledPane senderGroup = createSenderGroup();
        
        // Receiver section
        TitledPane receiverGroup = createReceiverGroup();
        
        mainContent.getChildren().addAll(keyGroup, senderGroup, receiverGroup);
        root.setCenter(mainContent);
        
        // Bottom section
        HBox bottomBox = createBottomBox();
        root.setBottom(bottomBox);

        // Configure window
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createTitleBox() {
        Label titleLabel = new Label("DIGITAL SIGNATURE SOFTWARE");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(15));
        titleBox.setStyle("-fx-background-color: #3498db; -fx-border-color: #2980b9; -fx-border-width: 0 0 2 0;");
        
        return titleBox;
    }
    
    private TitledPane createKeyGroup() {
        keySizeComboBox = new ComboBox<>();
        keySizeComboBox.getItems().addAll(256, 512, 1024, 2048);
        keySizeComboBox.setValue(1024);
        keySizeComboBox.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        
        Button generateKeyButton = new Button("Tạo khóa");
        generateKeyButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        generateKeyButton.setOnAction(e -> generateKeys());
        
        privateKeyArea = new TextArea();
        privateKeyArea.setPrefHeight(80);
        privateKeyArea.setWrapText(true);
        privateKeyArea.setStyle("-fx-control-inner-background: #fff8e1; -fx-border-color: #f39c12;");
        
        publicKeyArea = new TextArea();
        publicKeyArea.setPrefHeight(80);
        publicKeyArea.setWrapText(true);
        publicKeyArea.setStyle("-fx-control-inner-background: #e8f8f5; -fx-border-color: #1abc9c;");
        
        GridPane keyContent = new GridPane();
        keyContent.setVgap(10);
        keyContent.setHgap(10);
        keyContent.setPadding(new Insets(10));
        
        keyContent.add(new Label("Kích thước:"), 0, 0);
        keyContent.add(keySizeComboBox, 1, 0);
        keyContent.add(generateKeyButton, 2, 0);
        keyContent.add(new Label("Private Key:"), 0, 1);
        keyContent.add(privateKeyArea, 0, 2, 3, 1);
        keyContent.add(new Label("Public Key:"), 0, 3);
        keyContent.add(publicKeyArea, 0, 4, 3, 1);
        
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        col1.setPrefWidth(100);
        col2.setPrefWidth(150);
        col3.setHgrow(Priority.ALWAYS);
        keyContent.getColumnConstraints().addAll(col1, col2, col3);
        
        TitledPane keyGroup = new TitledPane();
        keyGroup.setText("KHÓA RSA");
        keyGroup.setContent(keyContent);
        keyGroup.setExpanded(true);
        keyGroup.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        return keyGroup;
    }

    private TitledPane createSenderGroup() {
        senderFileField = new TextField();
        senderFileField.setEditable(false);
        senderFileField.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        
        Button senderFileButton = new Button("Chọn tài liệu");
        senderFileButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        senderFileButton.setOnAction(e -> chooseFile(senderFileField, senderHashArea));
        
        senderHashArea = new TextArea();
        senderHashArea.setPrefHeight(60);
        senderHashArea.setEditable(false);
        senderHashArea.setStyle("-fx-control-inner-background: #f5f5f5; -fx-border-color: #bdc3c7;");
        
        Button createSignatureButton = new Button("Tạo chữ ký");
        createSignatureButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        createSignatureButton.setOnAction(e -> createSignature());
        
        signatureArea = new TextArea();
        signatureArea.setPrefHeight(80);
        signatureArea.setWrapText(true);
        signatureArea.setStyle("-fx-control-inner-background: #fff3e0; -fx-border-color: #f39c12;");
        
        GridPane senderContent = new GridPane();
        senderContent.setVgap(10);
        senderContent.setHgap(10);
        senderContent.setPadding(new Insets(10));
        
        senderContent.add(new Label("Đầu vào:"), 0, 0);
        senderContent.add(senderFileField, 1, 0);
        senderContent.add(senderFileButton, 2, 0);
        senderContent.add(new Label("Băm SHA-1:"), 0, 1);
        senderContent.add(senderHashArea, 0, 2, 3, 1);
        senderContent.add(createSignatureButton, 0, 3, 3, 1);
        senderContent.add(new Label("Chữ ký:"), 0, 4);
        senderContent.add(signatureArea, 0, 5, 3, 1);
        
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        col1.setPrefWidth(80);
        col2.setHgrow(Priority.ALWAYS);
        col3.setPrefWidth(120);
        senderContent.getColumnConstraints().addAll(col1, col2, col3);
        
        TitledPane senderGroup = new TitledPane();
        senderGroup.setText("NGƯỜI GỬI");
        senderGroup.setContent(senderContent);
        senderGroup.setExpanded(true);
        senderGroup.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        return senderGroup;
    }

    private TitledPane createReceiverGroup() {
        receiverFileField = new TextField();
        receiverFileField.setEditable(false);
        receiverFileField.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        
        Button receiverFileButton = new Button("Chọn tài liệu");
        receiverFileButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        receiverFileButton.setOnAction(e -> chooseFile(receiverFileField, receiverHashArea));
        
        receiverHashArea = new TextArea();
        receiverHashArea.setPrefHeight(60);
        receiverHashArea.setEditable(false);
        receiverHashArea.setStyle("-fx-control-inner-background: #f5f5f5; -fx-border-color: #bdc3c7;");
        
        Button verifyButton = new Button("Kiểm tra");
        verifyButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        verifyButton.setOnAction(e -> verifySignature());
        
        verificationResultField = new TextField();
        verificationResultField.setEditable(false);
        verificationResultField.setStyle("-fx-font-weight: bold;");
        
        GridPane receiverContent = new GridPane();
        receiverContent.setVgap(10);
        receiverContent.setHgap(10);
        receiverContent.setPadding(new Insets(10));
        
        receiverContent.add(new Label("Đầu vào:"), 0, 0);
        receiverContent.add(receiverFileField, 1, 0);
        receiverContent.add(receiverFileButton, 2, 0);
        receiverContent.add(new Label("Băm SHA-1:"), 0, 1);
        receiverContent.add(receiverHashArea, 0, 2, 3, 1);
        receiverContent.add(verifyButton, 0, 3, 3, 1);
        receiverContent.add(new Label("Kết quả:"), 0, 4);
        receiverContent.add(verificationResultField, 1, 4, 2, 1);
        
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        col1.setPrefWidth(80);
        col2.setHgrow(Priority.ALWAYS);
        col3.setPrefWidth(120);
        receiverContent.getColumnConstraints().addAll(col1, col2, col3);
        
        TitledPane receiverGroup = new TitledPane();
        receiverGroup.setText("NGƯỜI NHẬN");
        receiverGroup.setContent(receiverContent);
        receiverGroup.setExpanded(true);
        receiverGroup.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        return receiverGroup;
    }

    private HBox createBottomBox() {
        Button resetButton = new Button("RESET");
        resetButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        resetButton.setOnAction(e -> resetFields());
        
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setStyle("-fx-accent: #2ecc71;");
        
        Label footerLabel = new Label("Bản quyền thuộc về Đỗ Duy Toàn - CNTT02_K17_HAUI");
        footerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        footerLabel.setTextFill(Color.web("#7f8c8d"));
        
        HBox bottomBox = new HBox(15, resetButton, progressBar, footerLabel);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setStyle("-fx-background-color: #ecf0f1;");
        
        return bottomBox;
    }

    private void chooseFile(TextField fileField, TextArea hashArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tài liệu");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Tất cả files", "*.*"),
            new FileChooser.ExtensionFilter("Tài liệu", "*.docx", "*.pdf", "*.txt"),
            new FileChooser.ExtensionFilter("Ảnh", "*.jpg", "*.png", "*.jpeg")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            progressBar.setProgress(0.3);
            fileField.setText(selectedFile.getAbsolutePath());
            
            try {
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                progressBar.setProgress(0.6);
                String hash = RSAUtils_1.calculateSHA1(fileBytes);
                hashArea.setText(hash);
                progressBar.setProgress(1.0);
            } catch (Exception ex) {
                showAlert("Lỗi", "Không thể đọc file: " + ex.getMessage());
                progressBar.setProgress(0);
            }
        }
    }

    private void generateKeys() {
        int bitLength = keySizeComboBox.getValue();
        progressBar.setProgress(0.1);
        
        if (bitLength < 1024) {
            showAlert("Cảnh báo", "Khóa có kích thước nhỏ hơn 1024 bit có thể không an toàn!");
        }
        
        try {
            progressBar.setProgress(0.3);
            rsa.generateKeys(bitLength);
            
            progressBar.setProgress(0.9);
            privateKeyArea.setText("n: " + rsa.getN() + "\nd: " + rsa.getD());
            publicKeyArea.setText("n: " + rsa.getN() + "\ne: " + rsa.getE());
            progressBar.setProgress(1.0);
            
            showAlert("Thành công", "Đã tạo khóa thành công với kích thước " + bitLength + " bit");
        } catch (Exception ex) {
            showAlert("Lỗi", "Không thể tạo khóa: " + ex.getMessage());
            progressBar.setProgress(0);
        }
    }

    private void createSignature() {
        if (senderFileField.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn tài liệu để ký");
            return;
        }
        
        if (privateKeyArea.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng tạo khóa trước");
            return;
        }
        
        progressBar.setProgress(0.2);
        
        try {
            byte[] fileBytes = Files.readAllBytes(new File(senderFileField.getText()).toPath());
            progressBar.setProgress(0.5);
            
            senderHashArea.setText(RSAUtils_1.calculateSHA1(fileBytes));
            
            BigInteger signature = rsa.sign(fileBytes);
            signatureArea.setText(signature.toString());
            progressBar.setProgress(1.0);
            
            showAlert("Thành công", "Đã tạo chữ ký số thành công!");
        } catch (Exception ex) {
            showAlert("Lỗi", "Không thể tạo chữ ký: " + ex.getMessage());
            progressBar.setProgress(0);
        }
    }

    private void verifySignature() {
        if (receiverFileField.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn tài liệu để kiểm tra");
            return;
        }
        
        if (signatureArea.getText().isEmpty()) {
            showAlert("Lỗi", "Không có chữ ký để kiểm tra");
            return;
        }
        
        if (publicKeyArea.getText().isEmpty()) {
            showAlert("Lỗi", "Không có khóa công khai để kiểm tra");
            return;
        }
        
        progressBar.setProgress(0.2);
        
        try {
            byte[] fileBytes = Files.readAllBytes(new File(receiverFileField.getText()).toPath());
            progressBar.setProgress(0.4);
            
            receiverHashArea.setText(RSAUtils_1.calculateSHA1(fileBytes));
            
            BigInteger signature = new BigInteger(signatureArea.getText());
            boolean isValid = rsa.verify(fileBytes, signature);
            
            progressBar.setProgress(0.9);
            if (isValid) {
                verificationResultField.setText("Hợp lệ - Tài liệu toàn vẹn");
                verificationResultField.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                verificationResultField.setText("Không hợp lệ - Tài liệu đã bị thay đổi");
                verificationResultField.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
            progressBar.setProgress(1.0);
        } catch (Exception ex) {
            showAlert("Lỗi", "Không thể kiểm tra chữ ký: " + ex.getMessage());
            progressBar.setProgress(0);
        }
    }

    private void resetFields() {
        privateKeyArea.clear();
        publicKeyArea.clear();
        senderFileField.clear();
        receiverFileField.clear();
        senderHashArea.clear();
        receiverHashArea.clear();
        signatureArea.clear();
        verificationResultField.clear();
        verificationResultField.setStyle("");
        progressBar.setProgress(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}