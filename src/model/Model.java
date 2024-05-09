package model;

import java.util.Random;

/**
 * The Model class represents the game model for a battleship game.
 * It handles ship randomization, game functionality, and tracking of ship
 * status.
 */
public class Model {

    private int dimension;
    private int leftShipCount;
    private Ship[] firstShips;

    private int rightShipCount;
    private Ship[] secondShips;

    private int maxShipCount;
    private Ship currentShip;

    private String record;

    /**
     * Randomly positions ships for both players' boards.
     */
    public void randomizeShips() {
        record = "";
        randomizeShip(1);
        randomizeShip(2);
    }

    /**
     * Randomly positions ships on a specific player's board.
     *
     * @param boardId The ID of the player's board (1 for the first player, 2 for
     *                the second player).
     */
    public void randomizeShip(int boardId) {
        String[][] currentCoordinates = new String[dimension * 2 + 1][dimension * 2 + 1];

        generateShips(boardId);
        Random rand = new Random();

        for (Ship ship : boardId == 1 ? firstShips : secondShips) {
            boolean placed = false;
            while (!placed) {
                int randRow = 1 + rand.nextInt(2 * dimension);
                int randCol = 1 + rand.nextInt(2 * dimension);

                if (isSuitableForShip(currentCoordinates, ship, randRow, randCol)) {
                    ship.setCoordinates(randRow, randCol);
                    setSelectedCoordinate(currentCoordinates, ship);
                    placed = true;
                }
            }
        }

        leftShipCount = rightShipCount = maxShipCount = boardId == 1 ? firstShips.length : secondShips.length;
    }

    /**
     * Checks if a given position is suitable for placing a ship of specific length
     * on a board.
     *
     * @param currentCoordinates The current state of occupied coordinates on the
     *                           board.
     * @param ship               The ship to be placed.
     * @param randRow            The row for the potential ship placement.
     * @param randCol            The column for the potential ship placement.
     * @return {@code true} if the position is suitable for ship placement,
     *         {@code false} otherwise.
     */
    public boolean isSuitableForShip(String[][] currentCoordinates, Ship ship, int randRow, int randCol) {
        if (ship.isHorizontal()) {
            if (randCol + ship.getLength() > currentCoordinates[randRow].length) {
                return false; // Range exceeds the board dimensions
            }
            for (int i = randCol; i < randCol + ship.getLength(); i++) {
                if (currentCoordinates[randRow][i] != null) {
                    return false; // Range contains a coordinate that is already occupied
                }
            }
        } else {
            if (randRow + ship.getLength() > currentCoordinates.length) {
                return false; // Range exceeds the board dimensions
            }
            for (int i = randRow; i < randRow + ship.getLength(); i++) {
                if (currentCoordinates[i][randCol] != null) {
                    return false; // Range contains a coordinate that is already occupied
                }
            }
        }
        return true; // Range is suitable for placing a ship of the given length
    }

    /**
     * Generates ships for the specified player's board based on the dimension.
     *
     * @param boardId The ID of the player's board (1 for the first player, 2 for
     *                the second player).
     */
    public void generateShips(int boardId) {
        int index = 0;
        Ship[] currentBoardShips = new Ship[(dimension + 1) * dimension / 2];

        if (boardId == 1)
            firstShips = currentBoardShips;
        else
            secondShips = currentBoardShips;

        Random rand = new Random();
        for (int i = dimension; i >= 1; i--) {
            for (int j = 0; j < dimension - i + 1; j++) {
                currentBoardShips[index++] = new Ship(i, rand.nextBoolean());
            }
        }
    }

    /**
     * Sets selected coordinates on the board for a given ship.
     *
     * @param currentCoordinates The current state of occupied coordinates on the
     *                           board.
     * @param ship               The ship for which coordinates are being set.
     */
    public void setSelectedCoordinate(String[][] currentCoordinates, Ship ship) {
        int col = ship.getCol();
        int row = ship.getRow();
        int shipLength = ship.getLength();

        if (ship.isHorizontal()) {
            for (int i = col; i < col + shipLength; i++) {
                currentCoordinates[row][i] = Integer.toString(shipLength);
            }
        } else {
            for (int i = row; i < row + shipLength; i++) {
                currentCoordinates[i][col] = Integer.toString(shipLength);
            }
        }
    }

    /**
     * Gets an array of randomized ships for a specific player's board.
     *
     * @param boardId The ID of the player's board (1 for the first player, 2 for
     *                the second player).
     * @return An array of Ship objects representing the randomized ships.
     */
    public Ship[] getRandomizedShips(int boardId) {
        return boardId == 1 ? firstShips : secondShips;
    }

    // ==================== FOR GAME FUNCTIONALITY ====================

    /**
     * Receives a shot on the specified player's board and updates ship status
     * accordingly.
     *
     * @param targetId   The ID of the player's board receiving the shot.
     * @param coordinate The coordinate at which the shot is fired.
     * @return {@code true} if the shot hits a ship, {@code false} otherwise.
     */
    public boolean receiveShot(int targetId, String coordinate) {
        boolean hit = false;
        String playerName = targetId == 2 ? "FIRST PLAYER" : "SECOND PLAYER";
        for (Ship ship : targetId == 1 ? firstShips : secondShips) {
            for (String tempCoordinate : ship.getCoordinates()) {
                if (coordinate.equals(tempCoordinate)) {
                    hit = true;
                    ship.decreaseHealth();

                    currentShip = ship;

                    updateShipCount(targetId);
                    record = String.format("\n %s: %s (HIT) %s \n", playerName, coordinate, record);

                    return hit;
                }
            }
        }
        // currentShip = null;

        record = String.format("\n %s: %s (MISSED) %s \n", playerName, coordinate, record);

        return hit;
    }

    /**
     * Updates the ship count on the specified player's board after a ship is
     * destroyed.
     *
     * @param id The ID of the player's board (1 for the first player, 2 for the
     *           second player).
     */
    public void updateShipCount(int id) {
        if (currentShip.isDestroyed()) {
            if (id == 1) {
                leftShipCount--;
            } else {
                rightShipCount--;
            }
        }
    }

    /**
     * Calculates and returns the health percentage of the specified player's board.
     *
     * @param id The ID of the player's board (1 for the first player, 2 for the
     *           second player).
     * @return The health percentage of the player's board.
     */
    public int getBoardHealth(int id) {
        int nominator = (id == 1 ? leftShipCount : rightShipCount);

        float b = (((float) nominator / maxShipCount)) * 100;

        int c = (int) b;
        return c;
    }

    /**
     * Retrieves the current ship that was hit by the last shot.
     *
     * @return The Ship object representing the ship that was hit, or {@code null}
     *         if no ship was hit.
     */
    public Ship getCurrentShip() {
        return currentShip;
    }

    /**
     * Sets the dimension of the game board.
     *
     * @param dimension The dimension of the game board.
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * Generates and returns an array of Ship objects for a specific player's board.
     *
     * @param boardId The ID of the player's board (1 for the first player, 2 for
     *                the second player).
     * @return An array of Ship objects representing the generated ships.
     */
    public Ship[] getGeneratedShips(int boardId) {
        int index = 0;
        Ship[] currentBoardShips = new Ship[(dimension + 1) * dimension / 2];

        if (boardId == 1)
            firstShips = currentBoardShips;
        else
            secondShips = currentBoardShips;

        for (int i = dimension; i >= 1; i--) {
            for (int j = 0; j < dimension - i + 1; j++) {
                currentBoardShips[index++] = new Ship(i, true);
            }
        }

        return currentBoardShips;
    }

    /**
     * Gets the recorded game progress and hits/misses.
     *
     * @return The recorded game progress.
     */
    public String getRecord() {
        return record;
    }
}
