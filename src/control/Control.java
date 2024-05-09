package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import view.View;
import model.Model;
import model.Ship;

/**
 * The Control class acts as a controller in the battleship game,
 * managing user interactions, game logic, and communication.
 */
public class Control implements ActionListener {

    private View view;
    private Model model;
    private int currentTargetId;
    private int dimension;
    private ServerSocket serverSocket;
    private Ship[] firstPlayerShips;
    private Ship[] secondPlayerShips;
    private final int INVALID_SHOT = 0;
    private final int HIT = 1;
    private final int MISSED = 2;

    private String coordinateToUpdate;
    private boolean coordinateToUpdateHit;

    /**
     * Constructs a Control object with the associated view and model.
     *
     * @param view  The view representing the user interface.
     * @param model The model containing the game logic.
     */
    public Control(View view, Model model) {
        this.view = view;
        this.model = model;
        view.addController(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getstartButton()) {
            handleStartButton();
        } else if (e.getSource() == view.getendButton()) {
            handleEndButton();
        }
    }

    /**
     * Handles the start button click event.
     * Initializes the game and sets up the network communication.
     */
    public void handleStartButton() {
        try {
            dimension = view.getDimension() != 0 ? view.getDimension() : 1;
            model.setDimension(dimension);
            int port = Integer.parseInt(view.getPortNumber());
            openSocket(port);
        } catch (Exception e) {
            view.showError();
        }
    }

    /**
     * Handles the end button click event.
     * Exits the game application.
     */
    public void handleEndButton() {
        System.exit(0);
    }

    /**
     * Opens a socket for network communication and starts the game.
     *
     * @param serverPort The port number for the server socket.
     * @throws Exception If an error occurs during socket setup.
     */
    public void openSocket(int serverPort) throws Exception {
        serverSocket = null;

        try {
            model.randomizeShips();
            serverSocket = new ServerSocket(serverPort);

            // Accept connections and start the game
            acceptConnection(1);
            acceptConnection(2);

            String message = "Both players have connected. Let the battle begin!";
            view.appendMessageBox(message);

            view.disposeMenu();
            startGame();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Accepts a connection from a player, sends game data, and sets up
     * communication.
     *
     * @param id The ID of the player (1 or 2).
     * @throws Exception If an error occurs during communication setup.
     */
    public void acceptConnection(int id) throws Exception {
        Socket socket = serverSocket.accept();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(model.getRandomizedShips(id == 1 ? 1 : 2));
        objectOutputStream.writeObject(model.getRandomizedShips(id == 1 ? 2 : 1));
        objectOutputStream.flush();

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.writeInt(dimension);
        dataOutputStream.writeInt(id);
        dataOutputStream.flush();

        socket.close();
    }

    /**
     * Starts the game loop, handling player turns, updates, and communication.
     *
     * @throws Exception If an error occurs during communication or game logic.
     */
    public void startGame() throws Exception {
        currentTargetId = 2;
        boolean canPlay = true;
        while (canPlay) {
            Socket socket = serverSocket.accept();

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            int targetId = dataInputStream.readInt();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            String coordinate = (String) objectInputStream.readObject();

            if (targetId == currentTargetId) {
                int playerId = targetId == 2 ? 1 : 2;

                ObjectOutputStream objectOutputStream;
                boolean hit = validateShot(targetId, coordinate);
                dataOutputStream.writeInt(hit ? HIT : MISSED);

                objectOutputStream = new ObjectOutputStream(dataOutputStream);

                objectOutputStream.writeObject(model.getCurrentShip());

                // objectOutputStream.flush();

                currentTargetId = playerId;

                objectOutputStream.writeObject(coordinateToUpdate);

                dataOutputStream.writeBoolean(coordinateToUpdateHit);

                int targetHealth = model.getBoardHealth(targetId);

                int playerHealth = model.getBoardHealth(playerId);

                dataOutputStream.writeInt(targetHealth);
                // objectOutputStream.flush();

                dataOutputStream.writeInt(playerHealth);
                // objectOutputStream.flush();

                coordinateToUpdate = coordinate;

                coordinateToUpdateHit = hit;

                if (targetHealth == 0) {
                    view.showGameOver(playerId);
                    canPlay = false;
                } else if (playerHealth == 0) {
                    view.showGameOver(targetId);
                    canPlay = false;
                }

                objectOutputStream.writeObject(model.getRecord());

            } else {
                dataOutputStream.writeInt(INVALID_SHOT);
            }

            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
    }

    /**
     * Validates a shot fired by a player and updates game state.
     *
     * @param targetId   The ID of the targeted player (1 or 2).
     * @param coordinate The coordinate at which the shot is fired.
     * @return {@code true} if the shot hits a ship, {@code false} otherwise.
     * @throws Exception If an error occurs during game logic.
     */
    public boolean validateShot(int targetId, String coordinate) throws Exception {
        return model.receiveShot(targetId, coordinate);
    }
}
