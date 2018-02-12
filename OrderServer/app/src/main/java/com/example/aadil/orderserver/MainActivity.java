package com.example.aadil.orderserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private static boolean flag = false;
    private static String msg;
    private static Vector v = new Vector();

    private ArrayList<String> orders = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listView);
        setSupportActionBar(toolbar);

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, orders);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                msg = orders.get(i) + " ready";
                orders.remove(i);
                adapter.notifyDataSetChanged();
                flag = true;
            }
        });

        try{
            ServerSocket ss = new ServerSocket(8080);
            Reader r = new Reader(ss);
            r.start();

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    class Reader extends Thread{
        ServerSocket ss;
        Reader(ServerSocket ss){
            this.ss = ss;
        }

        public void run(){
            try{
                while(true) {
                    Socket s = ss.accept();
                    v.addElement(s);
                    DataInputStream din = new DataInputStream(s.getInputStream());
                    final String client_msg = din.readUTF();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            orders.add(client_msg);
                            adapter.notifyDataSetChanged();

                        }
                    });


                    Writer w = new Writer(s);
                    w.start();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Writer extends Thread{
        Socket s;
        Writer(Socket s){
            this.s = s;
        }

        public void run(){
            boolean flagx = true;
            while(flagx) {
                try {
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    if (flag) {
                        flag = false;
                        dout.writeUTF(msg);
                        s.close();
                        v.remove(s);
                        flagx = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
