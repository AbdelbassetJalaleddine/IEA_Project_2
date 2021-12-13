package iea.vaccum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import iea.vaccum.Adapters.TilesAdapter;
import iea.vaccum.CustomObjects.Tile;
import iea.vaccum.R;

class IntQ {

    private final int[] ar;
    private int front;
    private int end;
    private final int sz;

    // max_sz is the maximum number of items
    // that can be in the queue at any given time

    public IntQ(int max_sz) {
        front = end = 0;
        this.sz = max_sz + 1;
        ar = new int[sz];
    }

    public boolean isEmpty() {
        return front == end;
    }

    public int peek() {
        return ar[front];
    }

    // Add an element to the queue
    public void enqueue(int value) {
        ar[end] = value;
        if (++end == sz) end = 0;
        if (end == front) throw new RuntimeException("Queue too small!");
    }

    // Make sure you check is the queue is not empty before calling dequeue!
    public int dequeue() {
        int ret_val = ar[front];
        if (++front == sz) front = 0;
        return ret_val;
    }
}
public class MainActivity extends AppCompatActivity {

    // Creating Grid
    public int rows, cols, tiles;
    public char[][] m;
    int jjj,jj = 0,w =0;

    ArrayList<ArrayList<Integer>> allPaths = new ArrayList<>();
    ArrayList<Integer> partialdfsPath = new ArrayList<>();

    //Current coordinates
    public int s_row, s_col;
    public int d_row, d_col;

    //
    public ArrayList<Integer> path_check =new ArrayList<Integer>(tiles);
    public boolean abed_error;

    // Creating queues for x and y coordinates
    public IntQ row_q, col_q;

    // Variables used to track number of steps in BFS
    public int tiles_left_in_layer, tiles_in_next_layer;
    public int count;

    // Variables used to check if tiles are already visited or are our destination
    public boolean destination = false;
    public boolean[][] visited;

    // Variable used th stores the matrix of walls
    public int[][] walls;

    public int wallss;

    ArrayList<Integer> arrrrr = new ArrayList<>();

    private Handler trackingHandler = new Handler();

    ArrayList<Tile> tileList = new ArrayList<>();
    RecyclerView tilesRecyclerView;
    TilesAdapter tilesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rows = Integer.parseInt(getIntent().getStringExtra("rows"));
        cols = Integer.parseInt(getIntent().getStringExtra("cols"));
        wallss = Integer.parseInt(getIntent().getStringExtra("walls"));

        for(int i=0;i<rows*cols;i++){
            tileList.add(new Tile(false,false,false,false,false));
        }
        tilesRecyclerView = findViewById(R.id.tilesRecyclerView);



        try{
            init();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void init() throws InterruptedException {

        abed_error = false;


            tiles = rows * cols;
            m = new char[rows][cols];
            for (char[] chars : m) {
                //Whatever value you want to set them to
                Arrays.fill(chars, '.');
            }
            //Starting position

        //Enter x starting position of the vacuum
        s_col = Integer.parseInt(getIntent().getStringExtra("x_pos"));
        //Enter y starting position of the vacuum
        s_row = Integer.parseInt(getIntent().getStringExtra("y_pos"));

        int pos = s_col * cols + s_row;

        tileList.get(pos).setmVaccumCleaner(true);
            int select_env;

            //To select a  Fully Observable Environment  write
            //to select a Partially Observable Environment write
            if(getIntent().getBooleanExtra("fully_obs",false)){
                select_env = 0;
            }
            else{
                select_env = 1;
            }

            int dirt;
            //Write the number of Dirt desired

            dirt = Integer.parseInt(getIntent().getStringExtra("dirt"));

            if (dirt == 0)
                System.out.print("There are no dirty Tiles to Clean");

            for (int i = 0; i < dirt; i++)
                addDirt(m);

            tilesAdapter = new TilesAdapter(this,tileList);
            tilesRecyclerView.setLayoutManager(new GridLayoutManager(this, cols));
            tilesRecyclerView.setAdapter(tilesAdapter);

            int select =1;

            if (select_env==0) {
                // Write '0' for a Breadth-First Search Approach
                // Write '1' for a Depth-First Search Approach
                if(getIntent().getBooleanExtra("bfs",false)){
                    select = 0;
                }
                else{
                    select = 1;
                }
            }
            // Queue to store x and y coordinates in BFS
            row_q = new IntQ(tiles + 1);
            col_q = new IntQ(tiles + 1);

            visited = new boolean[rows][cols];
            for (boolean[] booleans : visited) {
                //Whatever value you want to set them to
                Arrays.fill(booleans, false);
            }
            //Walls matrix(total number of tiles, total number of tiles) 0 for no wall, and 1 for wall up
            walls = new int[tiles][tiles];
            for (int row = 0; row < m.length; row++) {
                for (int col = 0; col < m[row].length; col++) {
                    walls[row][col] = 0; //Whatever value you want to set them to
                }
            }
            add_walls(walls,wallss);
//            add_wall(walls, 7, 8);
//            add_wall(walls, 12, 17);
//            add_wall(walls, 10, 15);
//            add_wall(walls, 3, 4);
//            add_wall(walls,4,9);
//            add_wall(walls, 11, 16);
            print2D_walls(m);

//            m[0][4]='*';
        final Handler handler = new Handler(Looper.getMainLooper());
        int finalSelect = select;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                try{
                    if (select_env == 0) {

                        if (finalSelect == 0) {
                            bfs(m,s_row,s_col);

                        }
                        else if (finalSelect == 1) {
                            dfs(m, s_row, s_col);
                            counterTimerPartial();
                            if(!checkDirt(m)) {
                                System.out.println("Target can not be reached please check if any area is completely blocked");
                                abed_error=true;
                                popupError("Target can not be reached please check if any area is completely blocked");
                            }
                        }
                    }
                    else if (select_env == 1) {
                        partial_dfs(m, s_row, s_col);
                        counterTimerPartial();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, 100);



//            For single dfs
//            int[][] prev_dfs = new int[rows][cols];
//            prev_dfs = dfs_single(m,s_row, s_col, prev_dfs);
//            ArrayList<Integer> path = fully_backtrack(s_row, s_col, d_row, d_col, prev_dfs);
//            print2D_prev(prev_dfs);
//            play_path(path);

    }

    private void counterTimerPartial() {
        jj =0;
        new CountDownTimer(partialdfsPath.size() * 600, 600) {

            public void onTick(long millisUntilFinished) {
                if(jj < partialdfsPath.size()){
                    jjj = partialdfsPath.get(jj);
                    int k = jjj % cols;
                    int l = (jjj - k) / cols;
                    for (int i = 0; i < tileList.size(); i++) {
                        tileList.get(i).setmVaccumCleaner(false);
                    }
                    Log.d("position_vaccum", l * cols + k + "");
                    tileList.get(l * cols + k).setmVaccumCleaner(true);
                    tileList.get(l * cols + k).setmDirt(false);
                    tileList.get(l * cols + k).setmSelected(true);
                    tilesAdapter.notifyDataSetChanged();
                    jj++;
                }

            }

            public void onFinish() {
                /*Log.d("position_vaccum", "done");
                jj=0;
                w++;
                Log.d("position_vaccum","w : ) " + w);
                if(w < partialdfsPath.size() && w < rows * cols)
                    counterTimerPartial();*/
            }
        }.start();
    }

    private void popupError(String s) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitle(s);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmButton("OK", Dialog::dismiss);
        sweetAlertDialog.show();
    }

    // Print Tiles with walls and agent
    public void print2D_walls(char[][] mat, int r, int c){
        mat[r][c] = 'O';
        for (int p = 0; p < cols * 2 + 1; p++) {
            System.out.print("-");
        }
//        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.println();
            System.out.print("|");
            for (int j = 0; j < cols; j++) {
                int position = i * cols + j;
                boolean flag;
                if (position + 1 >= cols * rows)
                    flag = false;
                else
                    flag = walls[position][position + 1] == 1;
                if (flag) {
                    System.out.print(mat[i][j] + "|");
                } else
                    System.out.print(mat[i][j] + " ");
            }
            System.out.print("|");
            System.out.println();

            for (int j = 0; j < cols; j++) {
                int position = i * cols + j;
                boolean flag;
                if (position + cols >= cols * rows)
                    flag = false;
                else
                    flag = walls[i * cols + j][(i + 1) * cols + j] == 1;
                if (flag) {
                    System.out.print(" -");
                } else
                    System.out.print("  ");
            }
        }
        System.out.println();

        for (int p = 0; p < cols * 2 + 1; p++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.println();
        System.out.println();

        mat[r][c] = 'X';
    }

    public void print2D_walls(char[][] mat) throws InterruptedException{
        for (int p = 0; p < cols * 2 + 1; p++) {
            System.out.print("-");
        }
//        System.out.println();
        for (int i = 0; i < rows; i++) {
            System.out.println();
            System.out.print("|");
            for (int j = 0; j < cols; j++) {
                int position = i * cols + j;
                boolean flag;
                if (position + 1 >= cols * rows)
                    flag = false;
                else
                    flag = walls[position][position + 1] == 1;
                if (flag) {
                    System.out.print(mat[i][j] + "|");
                } else
                    System.out.print(mat[i][j] + " ");
            }
            System.out.print("|");
            System.out.println();

            for (int j = 0; j < cols; j++) {
                int position = i * cols + j;
                boolean flag;
                if (position + cols >= cols * rows)
                    flag = false;
                else
                    flag = walls[i * cols + j][(i + 1) * cols + j] == 1;
                if (flag) {
                    System.out.print(" -");
                } else
                    System.out.print("  ");
            }
        }
        System.out.println();

        for (int p = 0; p < cols * 2 + 1; p++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.println();
        System.out.println();

    }

    // Print previous nodes for each tiles
    public void print2D_prev(int[][] mat) {
        for (int[] row : mat)

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
    }

    // Print Tiles with agent
    public void print2D(char[][] mat, int r, int c) {
        mat[r][c] = 'O';
        for (char[] row : mat)

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
        System.out.println();
        mat[r][c] = '-';

    }

    // Method used to explore tiles neighboring the current tiles
    public void neighbors(int r, int c, IntQ row_q, IntQ col_q, int tiles_in_next_layer, int[][] prev) {
        // Vectors used for left, right, up and down
        int[] row_v = new int[]{-1, 1, 0, 0};
        int[] col_v = new int[]{0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            // Skip out of bounds locations
            int rr = r + row_v[i];
            int cc = c + col_v[i];
            if (rr < 0 || cc < 0)
                continue;
            if (rr >= rows || cc >= cols)
                continue;

            // Skip locations blocked by walls
            int position1 = r * cols + c;
            int position2 = rr * cols + cc;
            if (walls[position1][position2] == 1)
                continue;

            // Skip visited locations
            if (visited[rr][cc])
                continue;

            row_q.enqueue(rr);
            col_q.enqueue(cc);
            visited[rr][cc] = true;
            prev[rr][cc] = r * cols + c;
            tiles_in_next_layer++;
        }

    }


    public void par_neighbors(int r, int c, IntQ row_q, IntQ col_q, int tiles_in_next_layer, int[][] prev, int depth) {
        // Vectors used for left, right, up and down
        int[] row_v = new int[]{-1, 1, 0, 0};
        int[] col_v = new int[]{0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            // Skip out of bounds locations
            int rr = r + row_v[i];
            int cc = c + col_v[i];
            if (rr < 0 || cc < 0)
                continue;
            if (rr >= rows || cc >= cols)
                continue;

            // Skip locations blocked by walls
            int position1 = r * cols + c;
            int position2 = rr * cols + cc;
            if (walls[position1][position2] == 1)
                continue;

            // Skip visited locations
            if (visited[rr][cc])
                continue;

            row_q.enqueue(rr);
            col_q.enqueue(cc);
            visited[rr][cc] = true;
            prev[rr][cc] = r * cols + c;
            tiles_in_next_layer++;
        }

    }

    // To add walls (inputs are wrt the tiles number from the grid not x and y coordinates
    public void add_wall(int[][] matrix, int tile1, int tile2) {
        int r = tile1 - tile2;
        // 1 for wall up and 0 for no wall
        if (( r == cols || r == -cols)  ||
                ((r == 1 || r == -1)    &   (tile1/cols==tile2/cols))) {
            matrix[tile1][tile2] = 1;
            matrix[tile2][tile1] = 1;
            System.out.println("wall between tile " + tile1 + " and tile " + tile2 + " created");
        } else {
            System.out.println("no contact between these two tiles");
        }
    }

    // To add walls (inputs are wrt the tiles number from the grid not x and y coordinates
    public void add_vertical_wall(int[][] matrix) {
        // 1 for wall up and 0 for no wall
        Random random = new Random();
        int tile=random.nextInt(tiles-2);

        while((matrix[tile][tile + 1] == 1)||(tile%(cols) == (cols -1 ) ))
            tile=random.nextInt(tiles-2);
        matrix[tile][tile + 1] = 1;
        matrix[tile + 1][tile] = 1;
        tileList.get(tile).setmRightWall(true);
        System.out.println("wall between tile " + tile + " and tile " + (tile + 1) + " created");

    }

    public void add_horizontal_wall(int[][] matrix) {
        Random random = new Random();
        int tile=random.nextInt(tiles-cols-1);

        while((tile%(rows-1)==0)|| (matrix[tile][tile + cols] == 1)  )
            tile=random.nextInt(tiles-cols-1);
        matrix[tile][tile + cols] = 1;
        matrix[tile + cols][tile] = 1;
        tileList.get(tile).setmDownWall(true);
        System.out.println("wall between tile " + tile + " and tile " + (tile + cols) + " created");



    }

    public void add_walls (int[][] matrix,int n){
        for(int i =0; i<n/2;i++){
            add_horizontal_wall(matrix);
        }
        for(int j =0; j<(n-n/2);j++){
            add_vertical_wall(matrix);
//            ;
        }
    }


    // Solve using BFS for single and multiple dirty tiles
    public void single_bfs(char[][] matrix, int s_row, int s_col, int[][] prev) {
        count = 0;
        tiles_left_in_layer = 1;
        tiles_in_next_layer = 0;

        for (boolean[] booleans : visited) {
            //Whatever value you want to set them to
            Arrays.fill(booleans, false);
        }
        row_q = new IntQ(tiles + 1);
        col_q = new IntQ(tiles + 1);
        destination=false;

        row_q.enqueue((s_row));
        col_q.enqueue((s_col));
        visited[s_row][s_col] = true;

        while (!row_q.isEmpty()) {
            int r = row_q.dequeue();
            int c = col_q.dequeue();
            if (matrix[r][c] == '*') {
                destination = true;
                System.out.println();
                System.out.println();
                System.out.println();


                d_row=r;
                d_col=c;
//                System.out.println("r "+s_row +"   c "+s_col);
                break;
            }
            neighbors(r, c, row_q, col_q, tiles_in_next_layer, prev);

            tiles_left_in_layer--;
            if (tiles_left_in_layer == 0) {
                tiles_left_in_layer = tiles_in_next_layer;
                tiles_in_next_layer = 0;
                count++;
            }
//            print2D(matrix,r,c);
        }

    }

    // Solve using BFS for single and multiple dirty tiles
    public void par_bfs(char[][] matrix, int s_row, int s_col, int[][] prev, int depth) {
        count = 0;
        tiles_left_in_layer = 1;
        tiles_in_next_layer = 0;

        for (boolean[] booleans : visited) {
            //Whatever value you want to set them to
            Arrays.fill(booleans, false);
        }
        row_q = new IntQ(tiles + 1);
        col_q = new IntQ(tiles + 1);
        destination=false;

        row_q.enqueue((s_row));
        col_q.enqueue((s_col));
        visited[s_row][s_col] = true;

        while (!row_q.isEmpty()) {
            int r = row_q.dequeue();
            int c = col_q.dequeue();
            if (matrix[r][c] == '*') {
                destination = true;
                System.out.println();
                System.out.println();
                System.out.println();


                d_row=r;
                d_col=c;
//                System.out.println("r "+s_row +"   c "+s_col);
                break;
            }

            par_neighbors(r, c, row_q, col_q, tiles_in_next_layer, prev, depth);
            depth--;
            if (depth==0)
                break;
            tiles_left_in_layer--;
            if (tiles_left_in_layer == 0) {
                tiles_left_in_layer = tiles_in_next_layer;
                tiles_in_next_layer = 0;
                count++;
            }
//            print2D(matrix,r,c);
        }

    }


    public void bfs(char [][] matrix,int s_row, int s_col) throws InterruptedException {
        abed_error=false;
        while(!checkDirt(m)) {
            int[][] prev = new int[rows][cols];

            single_bfs(matrix, s_row, s_col, prev);
            ArrayList<Integer> path = fully_backtrack( s_row, s_col,d_row,d_col, prev);
            allPaths.add(path);
            play_path(path);
            Log.d("bfs_path",path.toString());
            s_row=d_row;
            s_col=d_col;

            //System.out.println(path_check+"  :  "+path);

            if (path_check.equals(path)){
                abed_error=true;
                System.out.println("Target can not be reached please check if any area is completely blocked");
                popupError("Target can not be reached please check if any area is completely blocked");
                break;
            }
            path_check= path;
        }

        runpath();

    }

    private void counterTimer() {
        new CountDownTimer(allPaths.get(w).size() * 600, 600) {

            public void onTick(long millisUntilFinished) {
                if(jj < allPaths.get(w).size()){
                    jjj = allPaths.get(w).get(jj);
                    int k = jjj % cols;
                    int l = (jjj - k) / cols;
                    for (int i = 0; i < tileList.size(); i++) {
                        tileList.get(i).setmVaccumCleaner(false);
                    }
                    Log.d("position_vaccum", l * cols + k + "");
                    Log.d("position_vaccum2", k * cols + l + "");
                    tileList.get(l * cols + k).setmVaccumCleaner(true);
                    tileList.get(l * cols + k).setmDirt(false);
                    tileList.get(l * cols + k).setmSelected(true);
                    tilesAdapter.notifyDataSetChanged();
                    jj++;
                }

            }

            public void onFinish() {
                Log.d("position_vaccum", "done");
                jj=0;
                w++;
                if(w < allPaths.size())
                    counterTimer();
            }
        }.start();
    }

    /*   private Runnable increaseJJJ = new Runnable() {
           @Override
           public void run() {
               int k = jjj % cols;
               int l = (jjj - k) / cols;
               for (int i = 0; i < tileList.size(); i++) {
                   tileList.get(i).setmVaccumCleaner(false);
               }
               Log.d("position_vaccum", l * rows + k + "");
               tileList.get(l * rows + k).setmVaccumCleaner(true);
               tileList.get(l * rows + k).setmDirt(false);
               tileList.get(l * rows + k).setmSelected(true);
               tilesAdapter.notifyDataSetChanged();
               //trackingHandler.postDelayed(trackProfile,0);
           }
       };*/
    private void runpath() {
        //trackingHandler.postDelayed(trackProfile,0);
        counterTimer();
        /*for(int j : allPaths.get(0)){
            int k = j % cols;
            int l = (jjj - k) / cols;
            for(int i=0;i<tileList.size();i++){
                tileList.get(i).setmVaccumCleaner(false);
            }
            Log.d("position_vaccum",l*rows + k + "");
            tileList.get(l*rows + k).setmVaccumCleaner(true);
            tileList.get(l*rows + k).setmDirt(false);
            //print2D_walls(m, l, k);
            tilesAdapter.notifyDataSetChanged();
        }*/
    }


    public void dfs_single(char[][] matrix, int s_row, int s_col, int[][] prev) {
        if (visited[s_row][s_col])
            return;
        visited[s_row][s_col] = true;
        int[] row_v = new int[]{-1, 1, 0, 0};
        int[] col_v = new int[]{0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            // Skip out of bounds locations
            int rr = s_row + row_v[i];
            int cc = s_col + col_v[i];
            if (rr < 0 || cc < 0)
                continue;
            if (rr >= rows || cc >= cols)
                continue;

            // Skip locations blocked by walls
            int position1 = s_row * cols + s_col;
            int position2 = rr * cols + cc;
            if (walls[position1][position2] == 1)
                continue;

            // Skip visited locations
            if (visited[rr][cc])
                continue;
            if (matrix[s_row][s_col] == '*') {
                destination = true;

                d_row= s_row;
                d_col= s_col;
                System.out.println("r "+s_row +"   c "+s_col);

                break;
            }
            dfs_single(matrix,rr, cc, prev);
            prev[rr][cc] = s_row * cols + s_col;

        }
    }

    // assuming that the agent can detect walls and dirty tiles directly next to it, and know the size of the room
    public void dfs(char[][] matrix, int s_row, int s_col) throws InterruptedException {

        partialdfsPath.add(s_row * cols + s_col);
        print2D_walls(matrix, s_row, s_col);
        if (visited[s_row][s_col])
            return;
        visited[s_row][s_col] = true;
        int[] row_v = new int[]{-1, +1, 0, 0};
        int[] col_v = new int[]{0, 0, 1, -1};

        for (int i = 0; i < 5; i++) {
            if (checkDirt(matrix))
                return;
            if (i == 4) {
                return;
            }

            // Skip out of bounds locations
            int rr = s_row + row_v[i];
            int cc = s_col + col_v[i];
            if (rr < 0 || cc < 0)
                continue;
            if (rr >= rows || cc >= cols)
                continue;

            // Skip locations blocked by walls
            int position1 = s_row * cols + s_col;
            int position2 = rr * cols + cc;
            if (walls[position1][position2] == 1) {
                continue;
            }

            // Skip visited locations
            if (visited[rr][cc]) {
                continue;
            }
            if (matrix[rr][cc] == '*') {
//                print2D_walls(matrix, rr, cc);
                System.out.println("Dirt at position x = "+ s_col +" y= "+ s_row +" cleaned");
            }

            dfs(m, rr, cc);
            if (checkDirt(matrix))
                return;
            else{
                partialdfsPath.add(s_row * cols + s_col);
                print2D_walls(matrix, s_row, s_col);
            }

        }



        dfs(m, s_row, s_col);
        partialdfsPath.add(s_row * cols + s_col);
        print2D_walls(matrix, s_row, s_col);


    }

    // assuming that the agent can detect walls and dirty tiles directly next to it, and know the size of the room
    public void partial_dfs(char[][] matrix, int s_row, int s_col) throws InterruptedException {


        partialdfsPath.add(s_row * cols + s_col);
        print2D_walls(matrix, s_row, s_col);
        if (visited[s_row][s_col])
            return;
        visited[s_row][s_col] = true;
        int[] row_v = new int[]{-1, +1, 0, 0};
        int[] col_v = new int[]{0, 0, 1, -1};

        for (int i = 0; i < 5; i++) {
            if (i == 4) {
                return;
            }

            // Skip out of bounds locations
            int rr = s_row + row_v[i];
            int cc = s_col + col_v[i];
            if (rr < 0 || cc < 0)
                continue;
            if (rr >= rows || cc >= cols)
                continue;

            // Skip locations blocked by walls
            int position1 = s_row * cols + s_col;
            int position2 = rr * cols + cc;
            if (walls[position1][position2] == 1) {
                continue;
            }

            // Skip visited locations
            if (visited[rr][cc]) {
                continue;
            }
            if (matrix[rr][cc] == '*') {
//                print2D_walls(matrix, rr, cc);
                System.out.println("Dirt at position x = "+ s_col +" y= "+ s_row +" cleaned");

            }

            partial_dfs(m, rr, cc);
            if (areAllTrue(visited))
                return;
            else
            {
                partialdfsPath.add(s_row * cols + s_col);

                print2D_walls(matrix, s_row, s_col);
            }

        }



        partial_dfs(m, s_row, s_col);


        partialdfsPath.add(s_row * cols + s_col);

        print2D_walls(matrix, s_row, s_col);


    }

    // Method used to get the shortest path in BFS
    public ArrayList<Integer> fully_backtrack(int s_r, int s_c, int d_r, int d_c, int[][] prev) {
        ArrayList<Integer> path = new ArrayList<Integer>(tiles);
        int k;
        int l;
        // Get coordinates form tile number
        int d = d_r * cols + d_c;
        int s = s_r * cols + s_c;

        // Reconstruct path going backward from the end
//        prev[s_r][s_c]=s;
        for (int j = d; j != s; j = prev[l][k]) {
            path.add(j);
            k = j % cols;
            l = (j - k) / cols;

        }
        path.add(s);


        Collections.reverse(path);
        // if our end and start are connected return the path
        return path;


    }


    // Print the matrix for each iteration of the shortest path
    private void play_path(ArrayList<Integer> arr) {
        for (int j : arr) {
            int k = j % cols;
            int l = (j - k) / cols;
            m[l][k] = 'O';
            m[l][k]= 'X';

            print2D_walls(m, l, k);
        }


    }


    private void addDirt(char[][] tiles) {
        Random random = new Random();
        int x = random.nextInt(tiles.length);
        int y = random.nextInt(tiles[0].length);
        int pos = x * cols + y;
        Log.d("dirt_position",pos + "");
        tileList.get(pos).setmDirt(true);
        while(tiles[x][y] !='*')
            tiles[x][y] ='*';

    }


    public boolean checkDirt(char[][] array){
        for (char[] chars : array) {
            for (char aChar : chars) {
                if (aChar == '*')
                    return false; //Whatever value you want to set them to
            }
        }
        return true;
    }


    public boolean areAllTrue(boolean[][] array){
        for (boolean[] booleans : array) {
            for (boolean aBoolean : booleans) {
                if (!aBoolean)
                    return false; //Whatever value you want to set them to
            }
        }
        return true;
    }
}