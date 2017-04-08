import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private final String filename;

    private final char FILL = '-';
    private final char PAINT = 'O';
    private final char STRIKE = 'X';
    private char[][] numberMatrix2;
    private char[][] numberMatrix;
    private char[][] paintMatrix;

    public Calculator(String filename) {
        this.filename = filename;
        createArray(getFile(filename));
        System.out.println("numberMatrix");
        printMatrix(numberMatrix);
    }

    private List<String> getFile(String filename) {
        List<String> lines = new ArrayList<>();
        String currentFile = "src/" + filename + ".txt";

        Path m59File = Paths.get(currentFile);
        try {
            lines = Files.readAllLines(m59File);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //        System.out.println("FROM FILE");
        //        for (String line : lines) {
        //            System.out.println(line);
        //        }

        return lines;
    }

    private void createArray(List<String> lines) {
        int width = lines.get(0).length();
        int height = lines.size();

        numberMatrix = new char[width][height];
        numberMatrix2 = new char[width][height];
        paintMatrix = new char[width][height];

        char[] charArray;
        int currentHeight = 0;
        int currentChar;
        for (String line : lines) {
            currentChar = 0;
            charArray = line.toCharArray();
            for (char c : charArray) {
                numberMatrix[currentHeight][currentChar] = c;
                currentChar++;
            }
            currentHeight++;
        }
    }

    private void printMatrix(char[][] matrix) {
        System.out.println();

        for (char[] line : matrix) {
            for (char c : line) {
                if (c == 0) {
                    System.out.print(' ');
                } else {
                    System.out.print(c);
                }
            }
            System.out.println();
        }
    }

    public void solve() {
        boolean isDone;

        do {
            isDone = true;

            //loop through numberMatrix
            for (int row = 0; row < numberMatrix.length; row++) {
                for (int col = 0; col < numberMatrix[row].length; col++) {

                    char c = numberMatrix[row][col];


                    //when no numbers or fills are found, algorithm is done.
                    if (Character.isDigit(c)) {
                        isDone = false;
                    }

                    if (Character.isDigit(c)) {
                        combinator(col, row);
                    }


                } //for x
            } //for y
            //            System.out.println("numberMatrix");
            //            printMatrix(numberMatrix);
            System.out.println("paintMatrix");
            printMatrix(paintMatrix);
        } while (!isDone);

        clearPainting();
        printMatrix(paintMatrix);
        saveToFile();
    }

    private void saveToFile() {
        String solvedFilename = "src/" + filename + "_solved.txt";
        try (FileWriter fw = new FileWriter(solvedFilename)) {

            for (int i = 0; i < paintMatrix.length; i++) {
                for (int j = 0; j < paintMatrix[i].length; j++) {
                    char nM = numberMatrix2[i][j];
                    char pM = paintMatrix[i][j];

                    if (pM == PAINT && Character.isDigit(nM)) {
                        fw.write(nM);
                    } else {
                        fw.write(pM);
                    }

                }
                fw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearPainting() {
        for (int i = 0; i < paintMatrix.length; i++) {
            for (int j = 0; j < paintMatrix[i].length; j++) {
                char c = paintMatrix[i][j];
                if (c == STRIKE) {
                    paintMatrix[i][j] = ' ';
                }
            }
        }
    }

    private void combinator(int col, int row) {
        int c = Character.getNumericValue(numberMatrix[row][col]);

        switch (c) {
            case 0:
                strikeSurround(col, row);
                break;
            default:
                doMagic(col, row);
                break;
        }
    }

    private void strikeSurround(int cCol, int cRow) {
        for (int row = cRow - 1; row < cRow + 2; row++) {
            for (int col = cCol - 1; col < cCol + 2; col++) {
                if (isInsideMatrix(col, row)) {
                    char nM = numberMatrix[row][col];
                    char pM = paintMatrix[row][col];
                    if ((nM == FILL || Character.isDigit(nM)) && pM != PAINT) {
                        paintMatrix[row][col] = STRIKE;
                    }
                }
            }
        }
        numberMatrix2[cRow][cCol] = numberMatrix[cRow][cCol];
        numberMatrix[cRow][cCol] = STRIKE;
    }

    private boolean isInsideMatrix(int col, int row) {
        return (row >= 0 && row < numberMatrix.length) && (col >= 0 && col < numberMatrix[row].length);
    }

    private void doMagic(int cCol, int cRow) {
        int[] count = countCells(cCol, cRow);//0:[fill/number count], 1:[paint count]
        int number = Character.getNumericValue(numberMatrix[cRow][cCol]);

        int available = count[0] + count[1];

        //available cells + painted cells
        if (available == number) {
            //all empty cells will be painted
            for (int row = cRow - 1; row < cRow + 2; row++) {
                for (int col = cCol - 1; col < cCol + 2; col++) {
                    if (isInsideMatrix(col, row)) {
                        char pC = paintMatrix[row][col];
                        if (pC != STRIKE) {
                            paintMatrix[row][col] = PAINT;
                            count[0]--;
                        }
                    }
                }
            }
        }
        if (available == 0 || count[1] == number) {
            strikeSurround(cCol, cRow);
        }
    }

    /**
     * @return 0:[fill/number count], 1:[paint count]
     */
    private int[] countCells(int cCol, int cRow) {
        int[] count = new int[]{0, 0};

        for (int row = cRow - 1; row < cRow + 2; row++) {
            for (int col = cCol - 1; col < cCol + 2; col++) {
                if (isInsideMatrix(col, row)) {
                    char nM = numberMatrix[row][col];
                    char pM = paintMatrix[row][col];
                    if ((nM == FILL || Character.isDigit(nM)) && pM == 0) {
                        count[0]++;
                    } else if (pM == PAINT) {
                        count[1]++;
                    }
                }
            }
        }
        return count;
    }
}
