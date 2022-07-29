package mc.challenge.maze;

public class ArrayUtil {

    private ArrayUtil() {
        throw new RuntimeException("may not instantiate this class");
    }

    public static void invertrows(char[][] arr) {
        int top = 0;
        int bottom = arr.length - 1;

        while (top < bottom) {
            var tmp = arr[top];
            arr[top] = arr[bottom];
            arr[bottom] = tmp;
            top++;
            bottom--;
        }
    }

    public static void invertrows(Object[][] arr) {
        int top = 0;
        int bottom = arr.length - 1;

        while (top < bottom) {
            var tmp = arr[top];
            arr[top] = arr[bottom];
            arr[bottom] = tmp;
            top++;
            bottom--;
        }
    }

    public static void invertcol(char[] arr) {
        int left = 0;
        int right = arr.length - 1;

        while (left < right) {
            var tmp = arr[left];
            arr[left] = arr[right];
            arr[right] = tmp;
            left++;
            right--;
        }
    }


    public static char[][] rotate(char[][] arrs) {
        var mx = new char[arrs[0].length][arrs.length];

        for (int r = 0; r < arrs.length; r++) {
            for (int c = 0; c < arrs[0].length; c++) {
                mx[c][r] = arrs[r][c];
            }
        }

        return mx;
    }

    public static boolean isInsideOuterWallsMatrix(int row, int col, Object[][] matrix) {
        return row >= 0 && row < matrix.length && col >= 0 && col < matrix[0].length;
    }

    public static boolean isInsideOuterWallsMatrix(int row, int col, char[][] matrix) {
        return row > 0 && row < matrix.length - 1 && col > 0 && col < matrix[0].length - 1;
    }

    public static final int[][] FOUR_DIRECTIONS = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };
}
