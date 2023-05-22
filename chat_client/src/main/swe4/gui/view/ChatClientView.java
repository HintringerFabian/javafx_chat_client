package main.swe4.gui.view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
	private ListView<Chat> chatPane;
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
	private Button lensButton;
	private TextField searchField;
	private VBox chatsPane;


	@Override
	public void start(Stage stage) throws Exception {
		primaryStage = stage;
		stage.setResizable(true);

		// Create the chatUI pane as a GridPane
		GridPane chatUI = createChatUiPane();

		// Create a new Scene and set it on the primary stage
		Scene scene = new Scene(chatUI, 1080, 720);

		stage.setOnCloseRequest(evt -> {
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
				evt.consume();
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
		chatPane = createChatSelectionPane();
		VBox pane = new VBox(userPane, chatPane);

		// TODO maybe i forgot to add a button to join a chat
		// Create the button
		Button addButton = new Button("+");
		addButton.getStyleClass().add("add-button");

		// Set the action handler for the button
		addButton.setOnAction(event -> {
			// Handle the button click event
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

	private void openNewChatPopup() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("New Chat");
		dialog.setHeaderText("Create a New Chat");

		// Create the chat name input field
		TextField chatNameField = new TextField();
		chatNameField.setPromptText("Enter chat name");

		// Create the dialog buttons
		ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

		// Enable/disable the create button based on input validation
		Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
		createButton.setDisable(true);

		// Validate the chat name field and admin selection
		chatNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			boolean isChatNameValid = !newValue.trim().isEmpty();
			createButton.setDisable(!isChatNameValid);
		});

		// Set the dialog content
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.addRow(0, new Label("Chat Name:"), chatNameField);

		dialog.getDialogPane().setContent(grid);

		// Request focus on the chat name field by default
		Platform.runLater(chatNameField::requestFocus);

		// Convert the result to a chat object when the create button is clicked
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == createButtonType) {
				String chatName = chatNameField.getText();
				chatPane.getItems().addAll(new Chat(chatName, user, null));
				chats.put(chatName, new Chat(chatName, user, null));
			}
			return null;
		});

		// Show the dialog and process the result
		dialog.showAndWait();
	}

	private HBox getChatHeaderPane(Image image, String name) {
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
		lensButton = new Button();
		lensButton.setId("lens-button");
		String lensPath = loadPicture("../images/lens.png");
		var lensImageView = new ImageView(new Image(lensPath));
		lensImageView.setFitHeight(20);
		lensImageView.setFitWidth(20);
		lensButton.setGraphic(lensImageView);
		lensButton.setOnAction(event -> {
			searchField = new TextField();
			searchField.setId("search-field");

			searchField.textProperty().addListener((observable, oldValue, newValue) -> {
				chatArea.getItems().clear();

				for (Message message : chats.get(currentChatName).getMessages()) {
					if (message.getMessage().contains(newValue)) {
						chatArea.getItems().add(message);
					}
				}
			});

			var children = headerPane.getChildren();

			if (children.contains(searchField)) {
				searchField.clear();
				children.remove(searchField);
			} else {
				children.add(searchField);
			}
		});
		lensButton.setVisible(!name.equals(""));
		headerPane.getChildren().addAll(chatPicture, chatNameText, lensButton);

		return headerPane;
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

	private ListView<Chat> createChatSelectionPane() {
		ListView<Chat> chatPane = new ListView<>();
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
					chatEditMenuItem = new MenuItem("Edit");
					chatDeleteMenuItem = new MenuItem("Delete");
					banUserMenuItem = new MenuItem("Ban User");
					// Add more menu items as needed

					// Set the action handlers for menu items
					chatEditMenuItem.setOnAction(event -> {
						// TODO This will be added in the next ue
					});

					chatDeleteMenuItem.setOnAction(event -> {
						chatToBeRemoved = item.getName();
						if (isCurrentUserAdmin()) {
							deleteChat(item);
						} else {
							showToast("You are not an admin");
						}
					});

					banUserMenuItem.setOnAction(event -> {
						// TODO This will be added in the next ue
						System.out.println("Ban user");
						showToast("User xyz has been banned");
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

		// Add event handler to handle chat selection
		chatPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


			if (newValue != null && !newValue.equals(oldValue)) {
				userPane = getChatHeaderPane(newValue.getImage(), newValue.getName());

				chatsPane.getChildren().remove(0);
				chatsPane.getChildren().add(0, userPane);

				chatArea.getItems().clear();

				// TODO pull into method
				ArrayList<Message> messages = chats.get(newValue.getName()).getMessages();
				for (Message message : messages) {
					chatArea.getItems().add(message);
				}

				currentChatName = newValue.getName();

				System.out.println("Selected chat: " + newValue.getName());
			}
		});

		return chatPane;
	}

	private boolean isCurrentUserAdmin() {
		// Use the currentUser variable to access the current user details
		// Return true if the current user is the admin, false otherwise
		Chat chat = chats.get(chatToBeRemoved);
		User admin = chat.getAdmin();

		return user.equals(admin);
	}

	private void deleteChat(Chat chat) {
		// TODO Add implementation for deleting the chat
		// Use the chat parameter to identify the chat to be deleted
		// Handle the deletion process accordingly
		// You can show a confirmation dialog or perform any other required actions
		System.out.println("Deleting chat: " + chat.getName());
		chats.remove(chat.getName());

		// update the chat selection pane
		chatPane.getItems().clear();
		chatPane.getItems().addAll(chats.values());
	}

	private VBox createChatPane() {
		userPane = getChatHeaderPane(null, null);

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

	private void showToast(String message) {
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
