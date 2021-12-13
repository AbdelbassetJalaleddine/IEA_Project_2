package iea.vaccum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import iea.vaccum.Adapters.TilesAdapter;
import iea.vaccum.CustomObjects.Tile;
import iea.vaccum.R;

import java.util.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

public class Project2Activity extends AppCompatActivity {
    public int wallss;
    RecyclerView tilesRecyclerView;
    TilesAdapter tilesAdapter;

    ArrayList<Tile> tileList = new ArrayList<>();


    // Creating Grid
    public static int rows, cols, tiles;
    public static char[][] m;

    //Current coordinates
    public static int d_row, d_col;


    public static int pos;

    //
    public static ArrayList<Integer> path_check = new ArrayList<>(tiles);
    public static boolean abed_error;
    // Creating queues for x and y coordinates
    public static IntQ row_q, col_q;

    // Variables used to track number of steps in BFS
    public static int tiles_left_in_layer, tiles_in_next_layer;
    public static int count;

    // Variables used to check if tiles are already visited or are our destination
    public static boolean destination = false;
    public static boolean[][] visited;

    // Variable used th stores the matrix of walls
    public static int[][] walls;



    public static int p_agents = 1;
    public static int n_agents = 2;
    public static int num_agents =0;
    public static int max_depth = 4 * num_agents;

    public static int[] ag_pos;

    public int county;
    int select;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Print Tiles with walls and agent

    public int cleanerss,dirtss,max_depthss,roundss,dirtProducers;
    public boolean minimaxChosen,algoChosen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project2);

        rows = Integer.parseInt(getIntent().getStringExtra("rows"));
        cols = Integer.parseInt(getIntent().getStringExtra("cols"));
        wallss = Integer.parseInt(getIntent().getStringExtra("walls"));


        try{
            dirtss = Integer.parseInt(getIntent().getStringExtra("dirt"));
        }catch (Exception e){
            dirtss = 3;
        }


        p_agents = Integer.parseInt(getIntent().getStringExtra("cleaners"));
        n_agents = Integer.parseInt(getIntent().getStringExtra("dirtProducers"));

        num_agents = n_agents + p_agents;
        ag_pos = new int[num_agents];

        try{
            max_depthss = Integer.parseInt(getIntent().getStringExtra("max_depth"));

            max_depth = max_depthss * num_agents;
        }catch (Exception e){

        }

        roundss = Integer.parseInt(getIntent().getStringExtra("rounds"));


        minimaxChosen = getIntent().getBooleanExtra("minimax",false);
        algoChosen = getIntent().getBooleanExtra("algo1",false);

        tilesRecyclerView = findViewById(R.id.tilesRecyclerView);

        for(int i=0;i<rows*cols;i++){
            tileList.add(new Tile(false,false,false,false,false,false));
        }

        tilesAdapter = new TilesAdapter(this,tileList,true);
        tilesRecyclerView.setLayoutManager(new GridLayoutManager(this, cols));
        tilesRecyclerView.setAdapter(tilesAdapter);
        try {
            main();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
        public void main() throws InterruptedException {
        abed_error = false;
        rows = rows;
        cols = cols;


        tiles = rows * cols;
        m = new char[rows][cols];
        for (char[] chars : m) {
            //Whatever value you want to set them to
            Arrays.fill(chars, '.');
        }

        int dirt;
        System.out.print("Write the number of Dirt desired");
        dirt = dirtss;
        if (dirt == 0)
            System.out.print("There are no dirty Tiles to Clean");

        for (int i = 0; i < dirt; i++)
            addDirt(m);

                /*m[0][0] = '*';
                m[0][2] = '*';
                m[2][0] = '*';
                m[2][2] = '*';*/

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

        //Initialise and set Walls
        int wal;
        wal = wallss;
        add_walls(walls, wal);


        Random random = new Random();

        // Initialize and Place Agents
        for (int i = 0; i < num_agents; i++) {
            boolean not = true;
            int temp = 0;
            while (not) {
                not = false;
                temp = random.nextInt(tiles - 1);
                for (int j : ag_pos)
                    if (j == temp) {
                        not = true;
                        break;
                    }
            }

            ag_pos[i] = temp;
//                ag_pos[i]=1;
            System.out.println("Agent : " + i + " Tile : " + ag_pos[i]);

            set_agent(m, i);
        }


        print2D_walls(m);
        select = 0;
        if(algoChosen){
            select = 0;
        }
        if(minimaxChosen){
            select = 1;
        }

        if (select == 0)
            algo1();
        else {
            algo2();
        }

        //TODO: work on end
        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
    }

        private void set_agent(char[][] mat, int agent) {
            int c = ag_pos[agent] % cols;
            int r = (ag_pos[agent] - c) / cols;

            char C = (char) (agent + '0');
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (mat[i][j] == C)
                        mat[i][j] = '.';//Whatever value you want to set them to
                }
            }
            mat[r][c] = C;
            if(minimaxChosen){
                Log.d("abed_print",C + "");

                if(agent < p_agents){
                    tileList.get(r * cols + c).setmVaccumCleaner(true);
                    tileList.get(r * cols + c).setmDirt(false);
                }
                else{
                    tileList.get(r * cols + c).setDirtProducers(true);
                    if ( county % 2 == 0){
                        tileList.get(r * cols + c).setmDirt(true);
                    }
                }
                tilesAdapter.notifyDataSetChanged();
            }

        }

        private static void set_mm_agent(char[][] temp_matrix, int agent, int r, int c) {
            char C = (char) (agent + '0');
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (temp_matrix[i][j] == C)
                        temp_matrix[i][j] = '.';//Whatever value you want to set them to
                }
            }
            temp_matrix[r][c] = C;

        }

        public void print2D_walls(char[][] mat) throws InterruptedException {

            TimeUnit.MILLISECONDS.sleep(500);

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

        public static void neighbors(char[][] matrix, int r, int c, IntQ row_q, IntQ col_q, int tiles_in_next_layer, int[][] prev) {
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
                if (matrix[rr][cc] != '.' & matrix[rr][cc] != 'X' & matrix[rr][cc] != '*')
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
        public  void add_vertical_wall(int[][] matrix) {
            // 1 for wall up and 0 for no wall
            Random random = new Random();
            int tile = random.nextInt(tiles - 2);


            while ((matrix[tile][tile + 1] == 1) || (tile % (cols) == (cols - 1)))
                tile = random.nextInt(tiles - 2);
            matrix[tile][tile + 1] = 1;
            matrix[tile + 1][tile] = 1;
//        System.out.println("wall between tile " + tile + " and tile " + (tile + 1) + " created");
            tileList.get(tile).setmRightWall(true);
            System.out.println("wall between tile " + tile + " and tile " + (tile + 1) + " created");
        }

        public void add_horizontal_wall(int[][] matrix) {
            Random random = new Random();
            int tile = random.nextInt(tiles - cols - 1);

            while ((tile % (rows - 1) == 0) || (matrix[tile][tile + cols] == 1))
                tile = random.nextInt(tiles - cols - 1);
            matrix[tile][tile + cols] = 1;
            matrix[tile + cols][tile] = 1;
//        System.out.println("wall between tile " + tile + " and tile " + (tile + cols) + " created");

            tileList.get(tile).setmDownWall(true);
            System.out.println("wall between tile " + tile + " and tile " + (tile + cols) + " created");
        }

        public void add_walls(int[][] matrix, int n) {
            for (int i = 0; i < n / 2; i++) {
                add_horizontal_wall(matrix);
            }
            for (int j = 0; j < (n - n / 2); j++) {
                add_vertical_wall(matrix);
//            ;
            }
        }

        // Solve using BFS for single and multiple dirty tiles
        public static void single_bfs(char[][] matrix, int s_row, int s_col, int[][] prev) {
            count = 0;
            tiles_left_in_layer = 1;
            tiles_in_next_layer = 0;

            for (boolean[] booleans : visited) {
                //Whatever value you want to set them to
                Arrays.fill(booleans, false);
            }
            row_q = new IntQ(tiles + 1);
            col_q = new IntQ(tiles + 1);
            destination = false;

            row_q.enqueue((s_row));
            col_q.enqueue((s_col));
            visited[s_row][s_col] = true;

            while (!row_q.isEmpty()) {
                int r = row_q.dequeue();
                int c = col_q.dequeue();
                if (matrix[r][c] == '*') {
                    matrix[r][c] = '.';
                    destination = true;
                    System.out.println();
                    System.out.println();
                    System.out.println();


                    d_row = r;
                    d_col = c;
//                System.out.println("r "+s_row +"   c "+s_col);
                    break;
                }
                neighbors(matrix, r, c, row_q, col_q, tiles_in_next_layer, prev);

                tiles_left_in_layer--;
                if (tiles_left_in_layer == 0) {
                    tiles_left_in_layer = tiles_in_next_layer;
                    tiles_in_next_layer = 0;
                    count++;
                }
//            print2D(matrix,r,c);
            }

        }

        // Method used to get the shortest path in BFS
        public static ArrayList<Integer> fully_backtrack(int s_r, int s_c, int d_r, int d_c, int[][] prev) {
            ArrayList<Integer> path = new ArrayList<>(tiles);
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

        private void addDirt(char[][] tiles) {
//        System.out.println("\nTime is passing and the hoover is not cleaning :( \nGetting Dirty :p");
            Random random = new Random();
            int x = random.nextInt(tiles.length);
            int y = random.nextInt(tiles[0].length);
            int pos = x * cols + y;
            /*Log.d("dirt_position",pos + "");
            tileList.get(pos).setmDirt(true);*/
            while (tiles[x][y] != '*')
                tiles[x][y] = '*';

        }

        public boolean checkDirt(char[][] array) {
            for (char[] chars : array) {
                for (char aChar : chars) {
                    if (aChar == '*')
                        return false; //Whatever value you want to set them to
                }
            }
            return true;
        }

        public boolean areAllTrue(boolean[][] array) {
            for (boolean[] booleans : array) {
                for (boolean aBoolean : booleans) {
                    if (!aBoolean)
                        return false; //Whatever value you want to set them to
                }
            }
            return true;
        }


        public void algo1() throws InterruptedException {
            Toast.makeText(this, "started algo 1", Toast.LENGTH_SHORT).show();
            final int[] count = {roundss};
                new CountDownTimer(count[0] * 1500, 1500) {

                    public void onTick(long millisUntilFinished) {
                        Log.d("onTick","algo1");
                        county = count[0];
                            int agent = 0;
                            char[][] temp_matrix = new char[rows][cols];
                            for(int w=0;w<tileList.size();w++){
                                tileList.get(w).setmVaccumCleaner(false);
                                tileList.get(w).setDirtProducers(false);
                            }
                            for (int i = 0; i < rows; i++) {
                                //Whatever value you want to set them to
                                if (cols >= 0) System.arraycopy(m[i], 0, temp_matrix[i], 0, cols);
                            }
                            while (agent < p_agents) {
                                pos = ag_pos[agent];
                                int c = pos % cols;
                                int r = (pos - c) / cols;
                                if (!checkDirt(m)){
                                    mm_bfs(temp_matrix, agent, r, c);
                                }
                                tileList.get(pos).setmVaccumCleaner(true);
                                tileList.get(pos).setmDirt(false);
                                agent++;

                            }
                            while (agent < num_agents) {
                                pos = ag_pos[agent];
                                int c = pos % cols;
                                int r = (pos - c) / cols;
                                if (count[0] % 2 == 0){
                                    m[r][c] = '*';
                                    tileList.get(pos).setmDirt(true);
                                }
                                tileList.get(pos).setDirtProducers(true);
                                dirty(agent, r, c);
                                agent++;

                            }

                            try {
                                print2D_walls(m);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            tilesAdapter.notifyDataSetChanged();
                            count[0]--;

                    }

                    public void onFinish() {

                    }
                }.start();

            }


        public void mm_bfs(char[][] matrix, int agent, int s_row, int s_col) {
            abed_error = false;

            int[][] prev = new int[rows][cols];

            single_bfs(matrix, s_row, s_col, prev);
            ArrayList<Integer> path = fully_backtrack(s_row, s_col, d_row, d_col, prev);
            System.out.println(path_check + "  :  " + path);
            if (path.size() < 2)
                System.out.println("The Whole Room is Clean");

            else {
                List<Integer> dy_path = path.subList(0, 2);

                int in = dy_path.get(1);

                ag_pos[agent] = in;
                set_agent(m, agent);

            }


            if (path_check.equals(path)) {
                abed_error = true;
                System.out.println("Target can not be reached please check if any area is completely blocked");
            }
            path_check = path;

        }


        public void dirty(int agent, int r, int c) {

            // Vectors used for left, right, up and down
            int[] row_v = new int[]{-1, 1, 0, 0};
            int[] col_v = new int[]{0, 0, 1, -1};
            double distance = 0;
            int sr;
            int sc;
            for (int i = 0; i < 4; i++) {
                // Skip out of bounds locations
                int rr = r + row_v[i];
                int cc = c + col_v[i];
                if (rr < 0 || cc < 0)
                    continue;
                if (rr >= rows || cc >= cols)
                    continue;
                if (m[rr][cc] != '.' & m[rr][cc] != 'X')
                    continue;
                // Skip locations blocked by walls
                int position1 = r * cols + c;
                int position2 = rr * cols + cc;
                if (walls[position1][position2] == 1)
                    continue;

                int sum_d = 0;
                for (int d = 0; d < p_agents; d++) {
                    int clean_agent = ag_pos[d];
                    int k = clean_agent % cols;
                    int l = (clean_agent - k) / cols;
                    double temp_d = Math.sqrt((rr - l) * (rr - l) + (cc - k) * (cc - k));
                    sum_d += temp_d;
                }
                if (sum_d > distance) {
                    sr = rr;
                    sc = cc;
                    ag_pos[agent] = sr * cols + sc;
                    distance = sum_d;
                }


            }

            set_agent(m, agent);
        }


        public M_arr minmax(char[][] temp_matrix, int agent, int depth) {

            agent = Math.abs(agent % num_agents);
            if (agent==0)
                count++;
            int c = ag_pos[agent] % cols;
            int r = ag_pos[agent] / cols;

            if (depth >= (max_depth)) {
                return score(temp_matrix);
            }

            if (agent < p_agents) {
                // Vectors used for left, right, up and down
                int[] row_v = new int[]{-1, 1, 0, 0};
                int[] col_v = new int[]{0, 0, 1, -1};
                double max = -100 * tiles;
                M_arr bb = new M_arr(0,0);
                for (int i = 0; i < 4; i++) {
                    char[][] temp1_m = new char[rows][cols];
                    for (int k = 0; k < rows; k++) {
                        //Whatever value you want to set them to
                        if (cols >= 0) System.arraycopy(temp_matrix[k], 0, temp1_m[k], 0, cols);
                    }
                    // Skip out of bounds locations
                    int rr = r + row_v[i];
                    int cc = c + col_v[i];
                    if (rr < 0 || cc < 0)
                        continue;
                    if (rr >= rows || cc >= cols)
                        continue;
                    if (temp_matrix[rr][cc] != '.' & temp_matrix[rr][cc] != 'X' & temp_matrix[rr][cc] != '*')
                        continue;

                    // Skip locations blocked by walls
                    int position1 = r * cols + c;
                    int position2 = rr * cols + cc;
                    if (walls[position1][position2] == 1)
                        continue;
                    set_mm_agent(temp1_m, agent, rr, cc);
                    ag_pos[agent] = rr * cols + cc;

                    agent++;
                    depth++;
                    M_arr obj = minmax(temp1_m, agent, depth);
                    obj.add(rr*cols+cc);
                    double c_max = obj.score;

                    depth--;
                    agent--;
                    ag_pos[agent] = r * cols + c;

                    if (c_max >= max) {
//                    minmax_p[depth] = rr * cols + cc;
                        bb=obj;
                        max = c_max;

                    }


                }
                return bb;
            } else {
                M_arr bb = new M_arr(0,0);
                // Vectors used for left, right, up and down
                int[] row_v = new int[]{-1, 1, 0, 0};
                int[] col_v = new int[]{0, 0, 1, -1};

                double min = tiles * 100;
                for (int i = 0; i < 4; i++) {
                    char[][] temp1_m = new char[rows][cols];
                    for (int k = 0; k < rows; k++) {
                        //Whatever value you want to set them to
                        if (cols >= 0) System.arraycopy(temp_matrix[k], 0, temp1_m[k], 0, cols);
                    }
                    // Skip out of bounds locations
                    int rr = r + row_v[i];
                    int cc = c + col_v[i];
                    if (rr < 0 || cc < 0)
                        continue;
                    if (rr >= rows || cc >= cols)
                        continue;
                    if (temp_matrix[rr][cc] != '.' & temp_matrix[rr][cc] != 'X')
                        continue;
                    // Skip locations blocked by walls
                    int position1 = r * cols + c;
                    int position2 = rr * cols + cc;
                    if (walls[position1][position2] == 1)
                        continue;
                    set_mm_agent(temp1_m, agent, rr, cc);
                    ag_pos[agent] = rr * cols + cc;
                    if (depth == agent)
                        m[r][c] = '*';
                    agent++;
                    depth++;
                    M_arr obj = minmax(temp1_m, agent, depth);
                    obj.add(rr*cols+cc);
                    double c_min = obj.score;
                    depth--;
                    agent--;
                    ag_pos[agent] = r * cols + c;

                    if (c_min <= min) {
//                    minmax_p[depth] = rr * cols + cc;
                        bb=obj;
                        min = c_min;
                    }
                }
                return bb;

            }

        }

        public M_arr score(char[][] matrix) {
            double score = 0;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (matrix[i][j] == '.')
                        score += 10;
                    else if (matrix[i][j] == '*')
                        score -= 100;
                }
            }

            return new M_arr(score);
        }

        public void algo2() throws InterruptedException {
            count =0;
            final int[] rounds = {roundss};
                new CountDownTimer((rounds[0]+max_depth) * 900, 900) {

                    public void onTick(long millisUntilFinished) {
                        M_arr obj_f =minmax(m, 0, 0);
                        System.out.println("size : " + obj_f.arr.size()+ " Score : "+ obj_f.score);
                        final int[] k = {0};
                            new CountDownTimer(obj_f.arr.size() * 600,600){
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    int i =obj_f.arr.size()-1;
                                    System.out.println(i);
                                    k[0] = k[0] % num_agents;
                                    System.out.println("agent = "+ k[0] +" , Position : "+obj_f.arr.get(i));
                                    ag_pos[k[0]] = obj_f.arr.get(i);
                                    set_agent(m, k[0]);
                                    tilesAdapter.notifyDataSetChanged();
                                    if (k[0] == num_agents-1) {
                                        try {
                                            print2D_walls(m);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    k[0]++;
                                    i--;
                                }

                                @Override
                                public void onFinish() {

                                }
                            }.start();

                        System.out.println("round over");
                        rounds[0]--;
                        tilesAdapter.notifyDataSetChanged();
                    }

                    public void onFinish() {

                    }
                }.start();
        }

        public void set_agents_from_grid (char [][]matrix){
            for (int i =0; i<num_agents; i++){

                char C = (char) (i + '0');
                for (int h = 0; h < rows; h++) {
                    for (int j = 0; j < cols; j++) {
                        if (matrix[h][j] == C)
                            ag_pos[i]=h*cols+j;
                    }
                }
            }
        }

        public class M_arr{
            ArrayList<Integer> arr = new ArrayList<>() ;
            double score;
            public M_arr(int a,double s) {
                arr.add(a) ;
                score = s ;
            }
            public void add(int a){
                arr.add(a);
            }
            public M_arr(double s) {
                score =s;
            }



        }




}