package main.swe4.gui.view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.swe4.gui.controller.EventListener;
import main.swe4.gui.model.Chat;
import main.swe4.gui.model.Message;
import main.swe4.gui.model.User;

import java.util.*;

public class ChatClientView extends Application {
	// TODO: in next ue, refactor this class to use a controller, and create a functioning MVC
	// as at the moment static data is used to simulate a database (in the view...)

	private Stage primaryStage;
	private TextField messageField;
	private Button sendButton;
	private ListView<Message> chatArea;
	private ListView<Chat> chatPane = new ListView<>();
	private final Map<String, Chat> chats = new HashMap<>();
	private User user;
	private String currentChatName;
	private ImageView chatPicture;
	private Text chatNameText;
	private HBox userPane;
	private String chatToBeRemoved;

	private MenuItem chatEditMenuItem;
	private MenuItem chatDeleteMenuItem;
	private MenuItem banUserMenuItem;
	private Button lensButton = new Button();
	private TextField searchField = new TextField();
	private VBox chatsPane;
	private EventListener eventListener;

	// chat creation variables
	private TextField createChatNameField = new TextField();
	private Dialog<ButtonType> createChatDialog = createChatCreationDialog();
	private Node chatCreateButtonNode;
	private ButtonType chatCreateButton;


	public void setEventListener(EventListener listener) {
		this.eventListener = listener;
	}

	@Override
	public void start(Stage stage) throws Exception {
		primaryStage = stage;
		stage.setResizable(true);

		// Create the chatUI pane as a GridPane
		GridPane chatUI = createChatUiPane();

		// Create a new Scene and set it on the primary stage
		Scene scene = new Scene(chatUI, 1080, 720);

		stage.setOnCloseRequest(event -> {
			// allow user to decide between yes and no
			Alert alert = new Alert(
					Alert.AlertType.CONFIRMATION,
					"If you close the window, the application will be closed and you will be logged out. Do you want to continue?",
					new ButtonType("Logout and close", ButtonBar.ButtonData.YES),
					ButtonType.NO
			);

			// clicking X also means no
			ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

			if (ButtonType.NO.equals(result)) {
				// consume event i.e. ignore close request
				event.consume();
			}
		});

		// set a minimum size for the stage
		stage.setMinWidth(800);
		stage.setMinHeight(600);

		// load the css file
		loadCss(scene);

		stage.setScene(scene);
		stage.setTitle("ChatBPT");
		stage.show();
	}

	private GridPane createChatUiPane() {
		GridPane root = new GridPane();
		root.setId("root-pane");

		// Add two columns to the GridPane
		// the first column should take up 35% of the width
		// the second column should take up 65% of the width
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(35);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(65);

		root.getColumnConstraints().addAll(col1, col2);

		// Add the panes to the root pane
		root.add(createUserChatSelectionPane(), 0, 1);
		root.add(createChatPane(), 1, 1);

		return root;
	}

	private VBox createUserChatSelectionPane() {
		HBox userPane = getHeaderPane();
		createChatSelectionPane(chatPane);
		VBox pane = new VBox(userPane, chatPane);

		// TODO maybe i forgot to add a button to join a chat
		// Create the button
		Button addButton = new Button("+");
		addButton.getStyleClass().add("add-button");

		// Set the action handler for the button
		addButton.setOnAction(event -> {
			// TODO Handle the button click event
			openNewChatPopup();
		});

		// Create a separate HBox for the button
		HBox buttonPane = new HBox(addButton);
		buttonPane.setAlignment(Pos.CENTER);
		buttonPane.setPadding(new Insets(10, 0, 0, 0));

		pane.getChildren().add(buttonPane);

		GridPane.setVgrow(pane, Priority.ALWAYS);

		return pane;
	}

	private Dialog<ButtonType> createChatCreationDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("New Chat");
		dialog.setHeaderText("Create a New Chat");

		// Create the chat name input field

		createChatNameField.setPromptText("Enter chat name");

		// Create the dialog buttons
		chatCreateButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(chatCreateButton, ButtonType.CANCEL);

		// Enable/disable the create button based on input validation

		chatCreateButtonNode = dialog.getDialogPane().lookupButton(chatCreateButton);
		chatCreateButtonNode.setDisable(true);

		// Set the dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.addRow(0, new Label("Chat Name:"), createChatNameField);

		dialog.getDialogPane().setContent(grid);

		// Request focus on the chat name field by default
		Platform.runLater(createChatNameField::requestFocus);

		return dialog;
	}

	public void setCreateChatButtonAction(Runnable action) {
		createChatDialog.setResultConverter(dialogButton -> {
			if (dialogButton == chatCreateButton) {
				action.run();
			}
			return null;
		});
	}
	public TextField getCreateChatNameField() {
		return createChatNameField;
	}

	public Node getChatCreateButtonNode() {
		return chatCreateButtonNode;
	}

	private void openNewChatPopup() {
		// Show the dialog and process the result
		createChatDialog.showAndWait();
	}

	public void setCreateChatNameValidation(ChangeListener<String> listener) {
		createChatNameField.textProperty().addListener(listener);
	}

	public HBox createChatHeaderPane(Image image, String name) {
		// TODO code duplication, maybe with an parameter we can unite the two headerpane methods?

		HBox headerPane = new HBox();
		headerPane.setPadding(new Insets(10));
		headerPane.setId("header-pane");

		// Add the user's profile picture
		chatPicture = new ImageView(image);
		chatPicture.setId("profile-pic");
		chatPicture.setFitHeight(50);
		chatPicture.setFitWidth(50);

		// Add the user's name
		if (name == null) {
			name = "";
		}
		chatNameText = new Text(name);
		chatNameText.setId("header-text");

		// Add the lens button
		lensButton.setId("lens-button");
		String lensPath = loadPicture("../images/lens.png");
		var lensImageView = new ImageView(new Image(lensPath));
		lensImageView.setFitHeight(20);
		lensImageView.setFitWidth(20);

		searchField.setId("search-field");

		lensButton.setGraphic(lensImageView);
		lensButton.setVisible(!name.equals(""));
		headerPane.getChildren().addAll(chatPicture, chatNameText, lensButton);

		return headerPane;
	}

	public void setMessageSearchAction(ChangeListener<String> listener) {
		searchField.textProperty().addListener(listener);
	}

	public void setLensButtonAction(EventHandler<ActionEvent> handler) {
		lensButton.setOnAction(handler);
	}

	public String getCurrentChatName() {
		return currentChatName;
	}

	public ListView<Message> getChatArea() {
		return chatArea;
	}

	public TextField getSearchField() {
		return searchField;
	}

	public HBox getUserPane() {
		return userPane;
	}

	private HBox getHeaderPane() {
		HBox headerPane = new HBox();
		headerPane.setPadding(new Insets(10));
		headerPane.setId("header-pane");

		// Add the user's profile picture
		ImageView profilePic = new ImageView(user.getPicture());
		profilePic.setFitHeight(50);
		profilePic.setFitWidth(50);
		profilePic.setId("profile-pic");
		headerPane.getChildren().add(profilePic);

		// Add the user's name
		Text userName = new Text(user.getUsername());

		userName.setId("header-pane");
		headerPane.getChildren().add(userName);
		return headerPane;
	}

	private void createChatSelectionPane(ListView<Chat> chatPane) {
		VBox.setVgrow(chatPane, Priority.ALWAYS);

		// Create some sample chats
		Image adminImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("../images/user.png")));

		// create a sample admin user
		User admin = new User("Admin user", "admin@adminmail.com", adminImage);

		Chat chat1 = new Chat("Drama Lama", admin, adminImage);
		Chat chat2 = new Chat("The office dudes and dudines", admin, adminImage);
		Chat chat3 = new Chat("Anime Chat", admin, adminImage);

		chatPane.getItems().addAll(chat1, chat2, chat3);
		chats.put(chat1.getName(), chat1);
		chats.put(chat2.getName(), chat2);
		chats.put(chat3.getName(), chat3);

		// Customize the appearance of each list cell
		chatPane.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(Chat item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setGraphic(null);
				} else {
					// Create a custom layout for each chat item
					HBox chatItem = new HBox();
					chatItem.setSpacing(10);
					chatItem.setAlignment(Pos.CENTER_LEFT);

					ImageView imageView = new ImageView(item.getImage());
					imageView.setFitWidth(25);
					imageView.setFitHeight(25);

					Text nameText = new Text(item.getName());
					nameText.setStyle("-fx-font-weight: bold;");
					HBox.setHgrow(nameText, Priority.ALWAYS);

					// Create the three dots button
					Button optionsButton = new Button("⋯"); // Three dots character
					optionsButton.setId("options-button");
					optionsButton.setAlignment(Pos.CENTER_RIGHT);

					// Create the popup menu
					ContextMenu contextMenu = new ContextMenu();
					chatDeleteMenuItem = new MenuItem("Delete");
					banUserMenuItem = new MenuItem("Ban User");
					// TODO change to unban user
					chatEditMenuItem = new MenuItem("Edit");
					// Add more menu items as needed

					// Set the action handlers for menu items
					// TODO This is functionality for the controller
					// but think about how to implement it because we need the "item" variable
					chatEditMenuItem.setOnAction(event -> {
						// TODO This will be added in the next ue
					});

					chatDeleteMenuItem.setOnAction(event -> eventListener.handleDeleteChat(item));

					// TODO This is functionality for the controller
					// but think about how to implement it because we need the "item" variable
					banUserMenuItem.setOnAction(event -> {
						// TODO This will be added in the next ue
						System.out.println("Ban user");
						showToast("User xyz has been banned from: " + item.getName());
						eventListener.handleBanUser(item);
					});

					// Add menu items to the popup menu
					contextMenu.getItems().addAll(chatEditMenuItem, chatDeleteMenuItem, banUserMenuItem);
					// Add more menu items to the contextMenu as needed

					// Show the popup menu when the optionsButton is clicked
					optionsButton.setOnAction(event -> contextMenu.show(optionsButton, Side.BOTTOM, 0, 0));

					chatItem.getChildren().addAll(imageView, nameText, new Region(), optionsButton);
					setGraphic(chatItem);
				}
			}
		});
	}

	public void setUserPane(HBox userPane) {
		this.userPane = userPane;
	}
	public void setCurrentChatName(String currentChatName) {
		this.currentChatName = currentChatName;
	}

	public void updateChatHeaderPane(HBox userPane) {
		chatsPane.getChildren().remove(0);
		chatsPane.getChildren().add(0, userPane);
	}

	public ListView<Chat> getChatPane() {
		return chatPane;
	}
	public void setChatPaneClickEvent(ChangeListener<Chat> listener) {
		chatPane.getSelectionModel().selectedItemProperty().addListener(listener);
	}

	private VBox createChatPane() {
		userPane = createChatHeaderPane(null, null);

		chatArea = new ListView<>();
		VBox.setVgrow(chatArea, Priority.ALWAYS);

		// Add some sample messages
		// create URI string from image file
		String uri = loadPicture("../images/user.png");

		User user1 = new User("User1", "user1@user.com", new Image(uri));
		User user2 = new User("User2", "user2@user.com,", new Image(uri));

		Message message1 = new Message(user1, "Hello", new Image(uri));
		Message message2 = new Message(user2, "Hi there", new Image(uri));
		Message message3 = new Message(user1, "How are you?", new Image(uri));

		chats.get("The office dudes and dudines").addMessage(message1);
		chats.get("The office dudes and dudines").addMessage(message2);
		chats.get("Drama Lama").addMessage(message3);

		// Customize the appearance of each list cell
		chatArea.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(Message item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setGraphic(null);
				} else {
					// Create a custom layout for each chat message
					VBox messagePane = new VBox();
					ImageView imageView = new ImageView(item.getPicture());
					imageView.setFitWidth(25); // Adjust the width as needed
					imageView.setFitHeight(25);
					TextFlow textFlow = new TextFlow(new Text(item.getUser().getUsername() + ": " + item.getMessage()));

					messagePane.getChildren().addAll(imageView, textFlow);
					setGraphic(messagePane);
				}
			}
		});

		HBox messageSendPane = createMessageSendPane();

		chatsPane = new VBox(userPane, chatArea, messageSendPane);
		GridPane.setVgrow(chatsPane, Priority.ALWAYS);

		return chatsPane;
	}

	private HBox createMessageSendPane() {
		messageField = new TextField();
		messageField.setId("message-field");
		messageField.setPromptText("Enter your message here");

		sendButton = new Button("Send");
		sendButton.setId("send-button");
		// TODO Thats functionality for the controller
		sendButton.setOnAction(event -> {
			String message = messageField.getText();

			if (message.isEmpty()) {
				showToast("Please enter a message");
			} else if (currentChatName == null) {
				showToast("Please select a chat to send a message");
			} else {
				chats.get(currentChatName).addMessage(new Message(user, message, user.getPicture()));
				// reload chat
				chatArea.getItems().clear();
				ArrayList<Message> messages = chats.get(currentChatName).getMessages();
				for (Message m : messages) {
					chatArea.getItems().add(m);
				}
				messageField.clear();
			}
		});

		HBox messageSendPane = new HBox(messageField, sendButton);
		messageSendPane.setId("message-send-pane");
		HBox.setHgrow(messageField, Priority.ALWAYS);

		return messageSendPane;
	}

	public void showToast(String message) {
		// Create a label for the toast message
		Label toastLabel = new Label(message);
		toastLabel.getStyleClass().add("toast-label");

		// Create a stack pane to hold the toast label
		StackPane toastPane = new StackPane(toastLabel);
		toastPane.getStyleClass().add("toast-pane");

		// Create a popup to show the toast
		Popup toastPopup = new Popup();
		toastPopup.getContent().add(toastPane);

		// Configure fade-in and fade-out animations
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), toastPane);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setOnFinished(event -> toastPopup.hide());

		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), toastPane);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);

		PauseTransition pause = new PauseTransition(Duration.seconds(2));
		pause.setOnFinished(event -> fadeOut.play());

		// Show the toast
		toastPopup.show(primaryStage);//, toastX, toastY);

		// Start fade-in animation
		fadeIn.play();
		pause.play();
	}

	private void loadCss(Scene scene) {
		try {
			scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../css/client.css")).toExternalForm());
		} catch (Exception e) {
			System.err.println("Could not load css file: " + e.getMessage());
		}
	}

	private String loadPicture(String path) {
		String uri = "";

		try {
			uri = Objects.requireNonNull(getClass().getResource(path)).toExternalForm();
		} catch (Exception e) {
			System.err.println("Could not load picture: " + e.getMessage());
		}

		return uri;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
