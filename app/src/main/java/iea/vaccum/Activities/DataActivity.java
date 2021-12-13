package iea.vaccum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import iea.vaccum.R;

public class DataActivity extends AppCompatActivity {

    EditText rows_EditText,cols_EditText,x_pos_EditText,y_pos_EditText,dirt_EditText,walls_EditText
            ,cleaners_EditText,dirt_producers_EditText,rounds_EditText,max_depth_EditText;
    RadioButton fully_observable_radio,partially_observable_radio;
    RadioButton bfs_radio,dfs_radio,
            minimax_radio,algo1_radio;
    TextView clean_textView;
    SwitchMaterial pick_project_radio;
    TableRow phase_1_x_pos,phase_1_y_pos,phase_1_nb_dirt,phase_1_observable_radio,phase_1_bfs_radio;
    TableRow phase_2_cleaners,phase_2_dirtProd,phase_2_max_depth,phase_2_rounds,phase_2_mini_radio;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        makeTableRows();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        pick_project_radio = findViewById(R.id.pick_project_radio);
        rows_EditText = findViewById(R.id.rows_EditText);
        cols_EditText = findViewById(R.id.cols_EditText);
        x_pos_EditText = findViewById(R.id.x_pos_EditText);
        y_pos_EditText = findViewById(R.id.y_pos_EditText);
        dirt_EditText = findViewById(R.id.dirt_EditText);
        walls_EditText = findViewById(R.id.walls_EditText);
        cleaners_EditText = findViewById(R.id.cleaners_EditText);
        dirt_producers_EditText = findViewById(R.id.dirt_producers_EditText);
        max_depth_EditText = findViewById(R.id.max_depth_EditText);
        rounds_EditText = findViewById(R.id.rounds_EditText);

        fully_observable_radio = findViewById(R.id.fully_observable_radio);
        partially_observable_radio = findViewById(R.id.partially_observable_radio);

        bfs_radio = findViewById(R.id.bfs_radio);
        dfs_radio = findViewById(R.id.dfs_radio);
        minimax_radio = findViewById(R.id.minimax_radio);
        algo1_radio = findViewById(R.id.algo1_radio);

        clean_textView = findViewById(R.id.clean_textView);

        pick_project_radio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                pick_project_radio.setText("Project 2");
                showProject2();
            }
            else
            {
                showProject1();
                pick_project_radio.setText("Project 1");
            }

        });
        fully_observable_radio.setOnCheckedChangeListener((buttonView, isChecked) -> partially_observable_radio.setChecked(false));

        partially_observable_radio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fully_observable_radio.setChecked(false);
            bfs_radio.setChecked(false);
            dfs_radio.setChecked(true);

        });

        bfs_radio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(partially_observable_radio.isChecked()){
                dfs_radio.setChecked(true);
                bfs_radio.setChecked(false);
            }
            else{
                dfs_radio.setChecked(false);
            }


        });

        minimax_radio.setOnCheckedChangeListener((buttonView, isChecked) -> algo1_radio.setChecked(false));
        algo1_radio.setOnCheckedChangeListener((buttonView, isChecked) -> minimax_radio.setChecked(false));
        dfs_radio.setOnCheckedChangeListener((buttonView, isChecked) -> bfs_radio.setChecked(false));

        clean_textView.setOnClickListener(v -> {
            if(!pick_project_radio.isChecked())
            {
                Intent mIntent = new Intent(this,MainActivity.class);
                mIntent.putExtra("rows",rows_EditText.getText().toString());
                mIntent.putExtra("cols",cols_EditText.getText().toString());
                mIntent.putExtra("walls",walls_EditText.getText().toString());


                mIntent.putExtra("x_pos",x_pos_EditText.getText().toString());
                mIntent.putExtra("y_pos",y_pos_EditText.getText().toString());

                mIntent.putExtra("dirt",dirt_EditText.getText().toString());

                mIntent.putExtra("fully_obs",fully_observable_radio.isChecked());
                mIntent.putExtra("partially_obs",partially_observable_radio.isChecked());


                mIntent.putExtra("bfs",bfs_radio.isChecked());
                mIntent.putExtra("dfs",dfs_radio.isChecked());
                startActivity(mIntent);
            }
            else
            {
                Intent mIntent = new Intent(this,Project2Activity.class);
                mIntent.putExtra("rows",rows_EditText.getText().toString());
                mIntent.putExtra("cols",cols_EditText.getText().toString());
                mIntent.putExtra("walls",walls_EditText.getText().toString());

                mIntent.putExtra("dirt",dirt_EditText.getText().toString());

                mIntent.putExtra("cleaners",cleaners_EditText.getText().toString());
                mIntent.putExtra("dirtProducers",dirt_producers_EditText.getText().toString());

                mIntent.putExtra("max_depth",max_depth_EditText.getText().toString());
                mIntent.putExtra("rounds",rounds_EditText.getText().toString());


                mIntent.putExtra("minimax",minimax_radio.isChecked());
                mIntent.putExtra("algo1",algo1_radio.isChecked());
                startActivity(mIntent);
            }
        });

    }

    private void makeTableRows() {
        phase_2_cleaners = findViewById(R.id.phase_2_cleaners);
        phase_2_dirtProd = findViewById(R.id.phase_2_dirtProd);
        phase_2_max_depth = findViewById(R.id.phase_2_max_depth);
        phase_2_rounds = findViewById(R.id.phase_2_rounds);
        phase_2_mini_radio = findViewById(R.id.phase_2_mini_radio);

        phase_1_x_pos = findViewById(R.id.phase_1_x_pos);
        phase_1_y_pos = findViewById(R.id.phase_1_y_pos);
        phase_1_nb_dirt = findViewById(R.id.phase_1_nb_dirt) ;
        phase_1_observable_radio = findViewById(R.id.phase_1_observable_radio);
        phase_1_bfs_radio = findViewById(R.id.phase_1_bfs_radio);
    }

    private void showProject1() {
        phase_2_cleaners.setVisibility(View.GONE);
        phase_2_dirtProd.setVisibility(View.GONE);
        phase_2_max_depth.setVisibility(View.GONE);
        phase_2_rounds.setVisibility(View.GONE);
        phase_2_mini_radio.setVisibility(View.GONE);

        phase_1_x_pos.setVisibility(View.VISIBLE);
        phase_1_y_pos.setVisibility(View.VISIBLE);
        phase_1_nb_dirt.setVisibility(View.VISIBLE);
        phase_1_observable_radio.setVisibility(View.VISIBLE);
        phase_1_bfs_radio.setVisibility(View.VISIBLE);
    }

    private void showProject2() {
        phase_2_cleaners.setVisibility(View.VISIBLE);
        phase_2_dirtProd.setVisibility(View.VISIBLE);
        phase_2_max_depth.setVisibility(View.VISIBLE);
        phase_2_rounds.setVisibility(View.VISIBLE);
        phase_2_mini_radio.setVisibility(View.VISIBLE);

        phase_1_x_pos.setVisibility(View.GONE);
        phase_1_y_pos.setVisibility(View.GONE);
        phase_1_nb_dirt.setVisibility(View.GONE);
        phase_1_observable_radio.setVisibility(View.GONE);
        phase_1_bfs_radio.setVisibility(View.GONE);
    }

}