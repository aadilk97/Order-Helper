package com.example.aadil.orderhelper;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private EditText msg, ip;
    private Button btn;
    private ListView lst;
    private static int c = -1;
    private static Socket s[] = new Socket[20];
    private ArrayList<String> orders = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msg = (EditText) findViewById(R.id.editText);
        ip = (EditText) findViewById(R.id.editText2);
        btn = (Button) findViewById(R.id.button);
        lst = (ListView) findViewById(R.id.listView);

        //orders.add("test");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c++;
                Writer obj = new Writer(msg.getText().toString(), ip.getText().toString());
                obj.start();
            }

        });


        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, orders);
        lst.setAdapter(adapter);

        lst.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                orders.remove(i);
                adapter.notifyDataSetChanged();
            }
        });

    }

    class Writer extends Thread{
        String message, ip;
        Writer(String message, String ip){
            this.message = message;
            this.ip = ip;
        }
        public void run(){
            try{
                s[++c] = new Socket(ip, 8080);
                DataOutputStream dout = new DataOutputStream(s[c].getOutputStream());

                dout.writeUTF(message);
                dout.flush();

                Reader r = new Reader(s[c], c);
                r.start();

            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Reader extends Thread{
        Socket s;
        int id;
        Reader(Socket s, int id){
            this.s = s;
            this.id = id;
        }
        public void run(){
            while (true) {
                try {
                    DataInputStream din = new DataInputStream(s.getInputStream());
                    final String server_msg = din.readUTF();

                    System.out.println("Server says " + server_msg + "for " + id);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast t = Toast.makeText(getApplicationContext(), server_msg, Toast.LENGTH_LONG);
                            t.show();
                            orders.add(server_msg);
                            adapter.notifyDataSetChanged();

                        }
                    });
//                    s.close();
//                    c--;

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

    }
}

