package br.ufjf.ieee.controlerobo;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Fonte
 * https://dragaosemchama.com/2015/05/programacao-bluetooth-no-android/
 * https://dragaosemchama.com/2016/04/comunicacao-bluetooth-entre-arduino-e-android/
 */

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ENABLE_BT = 15;
    private ConnectionThread connect;
    private EditText statusMessage;
    private Button btnConectar;
    private static String dataBuffer;
    private static int auxiliar_count=0;
    private int main_aux_count=0;
    private Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        statusMessage = (EditText) findViewById(R.id.statusMessage);
        btnConectar = (Button) this.findViewById(R.id.btnConectar);

        Button btnEsquerda = (Button) this.findViewById(R.id.btnEsq);
        Button btnDireita = (Button) this.findViewById(R.id.btnDir);
        Button btnFrente = (Button) this.findViewById(R.id.btnFrente);
        Button btnReverso = (Button) this.findViewById(R.id.btnRev);


        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnConectar.getText().toString().equals(getResources().getString(R.string.botao_conectar))){
                    conectarArduino();
                } else {
                    if(connect.isConnected()) {
                        connect.cancel();
                    }
                    btnConectar.setText(getResources().getString(R.string.botao_conectar));
                    btnConectar.setEnabled(true);
                }
            }
        });
        btnConectar.setEnabled(false);

        btnEsquerda.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {

                    enviarComando("E");
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {

                    enviarComando("P");
                    return true;
                }
                return false;
            }
        });

        btnDireita.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {

                    enviarComando("D");
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {

                    enviarComando("P");
                    return true;
                }
                return false;
            }
        });

        btnFrente.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {

                    enviarComando("F");
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {

                    enviarComando("P");
                    return true;
                }
                return false;
            }
        });

        btnReverso.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {

                    enviarComando("R");
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {

                    enviarComando("P");
                    return true;
                }
                return false;
            }
        });


        iniciaBluetooth();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    /**
     *
     * Bluetooth
     *
     */

    private void iniciaBluetooth(){


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "O dispositivo não tem suporte a Bluetooth", Toast.LENGTH_SHORT).show();

        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                //bluetooth está desabilitado ou não há autorização para o funcionamento
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                //bluetooth autorizado
                mBluetoothAdapter.enable();
                conectarArduino();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ENABLE_BT){

            if(resultCode == RESULT_OK) {
                //bluetooth autorizado
                conectarArduino();
            } else {
                Toast.makeText(this, "Autorização negada", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void conectarArduino(){

        btnConectar.setEnabled(false);

        //Galaxy S4 Thiago -> BC:72:B1:80:C6:CB
        //Galaxy J2 Thiago -> 30:CB:F8:06:5C:CA

        connect = new ConnectionThread("30:CB:F8:06:5C:CA");//MAC Address do módulo bluetooth
        connect.start();

        /* Um descanso rápido, para evitar bugs esquisitos.
         */
        try {
            Thread.sleep(1000);
        } catch (Exception E) {
            E.printStackTrace();
        }

    }

    /*
    Usando o timer para criar um listener para o método estático
    não é o método mais bonito (gambiarra) mas ta funcionando
     */
    private void startTimer(){
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerAux();
            }

        }, 0, 10);
    }
    private void stopTimer(){
        myTimer.cancel();
        myTimer.purge();
        myTimer=null;
    }

    /* move acesso ao metodo timerCheck pra UIThread */
    private void timerAux(){
        this.runOnUiThread(timerTick);
    }
    private Runnable timerTick = new Runnable() {
        public void run() {
            timerCheck();
        }
    };

    private void timerCheck(){
        if(auxiliar_count != main_aux_count) {//passa somente se houver uma chamada no Handler

            main_aux_count = auxiliar_count;

            if (dataBuffer.equals("---N")) {
                statusMessage.append(getResources().getString(R.string.erro_conexao) + "\n");
                btnConectar.setText(getResources().getString(R.string.botao_conectar));
            } else if (dataBuffer.equals("---S")) {
                statusMessage.append(getResources().getString(R.string.conectado) + "\n");
                btnConectar.setText(getResources().getString(R.string.botao_desconectar));
            } else {
                statusMessage.append("<< " + dataBuffer + "\n");
            }

            btnConectar.setEnabled(true);
        }
    }

    /*
    handler estático que recebe os dados do ConnectionThread
     */
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            /* Esse método é invocado na Activity principal
                sempre que a thread de conexão Bluetooth recebe
                uma mensagem.
             */
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = "";

            if(data != null){
                dataString = new String(data);
            }

            dataBuffer = dataString;

            auxiliar_count++;

        }
    };

    /*
    Formata e envia os comandos pelo bluetooth
     */
    private void enviarComando(String comando){
        String data = comando + "\r\n";
        connect.write(data.getBytes());
        statusMessage.append(">> " + comando + "\n");
    }

}
