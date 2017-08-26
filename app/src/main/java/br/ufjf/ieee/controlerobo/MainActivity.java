package br.ufjf.ieee.controlerobo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEsquerda = (Button) this.findViewById(R.id.btnEsq);
        Button btnDireita = (Button) this.findViewById(R.id.btnDir);
        Button btnFrente = (Button) this.findViewById(R.id.btnFrente);
        Button btnReverso = (Button) this.findViewById(R.id.btnRev);


        btnEsquerda.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {

                    enviarComando('E');
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {

                    enviarComando('E');
                    return true;
                }
                return false;
            }
        });

        btnDireita.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {

                    enviarComando('D');
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {

                    enviarComando('D');
                    return true;
                }
                return false;
            }
        });

        btnFrente.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {

                    enviarComando('F');
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {

                    enviarComando('F');
                    return true;
                }
                return false;
            }
        });

        btnReverso.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {

                    enviarComando('R');
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {

                    enviarComando('R');
                    return true;
                }
                return false;
            }
        });
    }

    private void enviarComando(char comando){

    }
}
