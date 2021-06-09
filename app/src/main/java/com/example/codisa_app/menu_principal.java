package com.example.codisa_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import java.security.spec.ECField;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import Utilidades.Stkw002List;
import Utilidades.controles;
import Utilidades.variables;
import androidx.appcompat.app.AppCompatActivity;

public class menu_principal extends AppCompatActivity {
    public static ProgressDialog prodialog;
    int contador=0;
    public void onBackPressed()  {
        Utilidades.controles.volver_atras(this,this, com.example.codisa_app.login.class,"DESEA SALIR DE LA APLICACION?",3);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);
        getSupportActionBar().setTitle("USUARIO:"+ variables.NOMBRE_LOGIN);
        getSupportActionBar().setSubtitle("SUCURSAL: "+ variables.DESCRIPCION_SUCURSAL_LOGIN);
        controles.conexion_sqlite(this);

        String[] array_opciones=variables.contenedor_menu.split(",");

        for(int i=0; i<array_opciones.length; i++)
        {
            int ID_BOTONES = getResources().getIdentifier(array_opciones[i], "id", getPackageName());
            Button stock = ((Button) findViewById(ID_BOTONES));

            stock.setVisibility(View.VISIBLE);
        }

    }

    public void ir_stkw002(View v){
        Intent i=new Intent(this,lista_stkw002_inv.class);
        startActivity(i);
    }

    public void ir_stkw001(View v){


        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("ATENCION!!!.")
                .setMessage("SELECCIONE EL TIPO DE INVENTARIO QUE DESEA GENERAR")
                .setPositiveButton("SELECCION MANUAL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        variables.titulo_stkw001="GENERACION DE TOMAS DE INVENTARIO MANUAL";
                        variables.tipo_stkw001=1;
                        variables.tipo_stkw001_insert="M";

                        Intent intent = new Intent(menu_principal.this, stkw001.class);
                        finish();
                        startActivity(intent);

                    }
                })
                .setNeutralButton("SELECCION AUTOMATICA",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        variables.titulo_stkw001="GENERACION DE TOMAS DE INVENTARIO AUTOMATICA";
                        variables.tipo_stkw001=2;
                        variables.tipo_stkw001_insert="C";
                        Intent intent = new Intent(menu_principal.this, stkw001.class);
                        finish();
                        startActivity(intent);

                    }
                })
                .show();

    }




    public void ir_test(View v){
        importar_inventario();
    }

    private void importar_inventario(){

        try {
            controles.connect = controles.conexion.Connections();
            Statement stmt = controles.connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) as  contador " +
                    "                               " +
                    "                    FROM   " +
                    "                       V_WEB_ARTICULOS_CLASIFICACION  a   " +
                    "                       inner join WEB_INVENTARIO_det b on a.arde_lote=b.winvd_lote  " +
                    "                       and a.ART_CODIGO=b.winvd_art       " +
                    "                       and a.SECC_CODIGO=b.winvd_secc     " +
                    "                       and a.ARDE_FEC_VTO_LOTE=b.winvd_fec_vto   " +
                    "                       inner join  WEB_INVENTARIO c on b.winvd_nro_inv=c.winve_numero  " +
                    "                       and c.winve_dep=a.ARDE_DEP  " +
                    "                       and c.winve_area=a.AREA_CODIGO  " +
                    "                       and c.winve_suc=a.ARDE_SUC   " +
                    "                       and c.winve_secc=a.SECC_CODIGO  " +
                    "                       where c.winve_empr=1 and a.ARDE_SUC="+variables.ID_SUCURSAL_LOGIN+"");

            while (rs.next())
            {
               contador=rs.getInt("contador");
            }
            new AlertDialog.Builder(menu_principal.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("SINCRONIZACION.")
                    .setMessage("DESEA SINCRONIZAR ANIMALES EN INVENTARIO ACTUALIZADOS")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            menu_principal.prodialog =  new ProgressDialog( menu_principal.this);
                            menu_principal.prodialog.setMax(contador);
                            LayerDrawable progressBarDrawable = new LayerDrawable(
                                    new Drawable[]{
                                            new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                    new int[]{Color.parseColor("black"),Color.parseColor("black")}),
                                            new ClipDrawable(
                                                    new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                            new int[]{Color.parseColor("yellow"),Color.parseColor("yellow")}),
                                                    Gravity.START,
                                                    ClipDrawable.HORIZONTAL),
                                            new ClipDrawable(
                                                    new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                            new int[]{Color.parseColor("yellow"),Color.parseColor("yellow")}),
                                                    Gravity.START,
                                                    ClipDrawable.HORIZONTAL)
                                    });
                            progressBarDrawable.setId(0,android.R.id.background);
                            progressBarDrawable.setId(1,android.R.id.secondaryProgress);
                            progressBarDrawable.setId(2,android.R.id.progress);
                            menu_principal.prodialog.setTitle("SINCRONIZANDO ANIMALES ACTUALIZADOS");
                            menu_principal.prodialog.setMessage("DESCARGANDO...");
                            menu_principal.prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            menu_principal.prodialog.setProgressDrawable(progressBarDrawable);
                            menu_principal.prodialog.show();
                            menu_principal.prodialog.setCanceledOnTouchOutside(false);
                            menu_principal.prodialog.setCancelable(false);
                            final AsyncImportador task = new  AsyncImportador();
                            task.execute();

                        }
                    })
                    .setNegativeButton("NO", null)
                    .show();
            }
        catch (Exception e){

        }

    }

    private void test(){
        try {
            SQLiteDatabase db1= controles.conSqlite.getReadableDatabase();
            db1.execSQL("delete from STKW002INV  WHERE estado='A'");
            db1.close();
            controles.connect = controles.conexion.Connections();
            Statement stmt = controles.connect.createStatement();

            ResultSet rs = stmt.executeQuery("" +
                    "   SELECT " +
                    "       a.ARDE_SUC, b.winvd_nro_inv, b.winvd_art,a.ART_DESC,b.winvd_lote,b.winvd_fec_vto,b.winvd_area,  " +
                    "       b.winvd_dpto,b.winvd_secc,b.winvd_flia,b.winvd_grupo,b.winvd_cant_act,c.winve_fec," +
                    "       dpto_desc,secc_desc,flia_desc,grup_desc,area_desc " +
                    "   FROM   " +
                    "       V_WEB_ARTICULOS_CLASIFICACION  a   " +
                    "       inner join WEB_INVENTARIO_det b on a.arde_lote=b.winvd_lote  " +
                    "       and a.ART_CODIGO=b.winvd_art       " +
                    "       and a.SECC_CODIGO=b.winvd_secc     " +
                    "       and a.ARDE_FEC_VTO_LOTE=b.winvd_fec_vto   " +
                    "       inner join  WEB_INVENTARIO c on b.winvd_nro_inv=c.winve_numero  " +
                    "       and c.winve_dep=a.ARDE_DEP  " +
                    "       and c.winve_area=a.AREA_CODIGO  " +
                    "       and c.winve_suc=a.ARDE_SUC   " +
                    "       and c.winve_secc=a.SECC_CODIGO  " +
                    "   where " +
                    "       c.winve_empr=1 " +
                    "       and a.ARDE_SUC="+variables.ID_SUCURSAL_LOGIN+"");
            int i=1;
        while (rs.next())
        {
            SQLiteDatabase db_consulta= controles.conSqlite.getReadableDatabase();
            Cursor cursor=db_consulta.rawQuery("select * from STKW002INV where  winvd_nro_inv ='"+rs.getInt("winvd_nro_inv")+"' and estado='P'" ,null);
            if (cursor.moveToNext())
            {

            }
            else {
                SQLiteDatabase db=controles.conSqlite.getReadableDatabase();


                db.execSQL(" INSERT INTO  STKW002INV (" +
                        "ARDE_SUC," +
                        "winvd_nro_inv," +
                        "winvd_art," +
                        "ART_DESC," +
                        "winvd_lote," +
                        "winvd_fec_vto," +
                        "winvd_area," +
                        "winvd_dpto," +
                        "winvd_secc," +
                        "winvd_flia," +
                        "winvd_grupo," +
                        "winvd_cant_act," +
                        "winve_fec," +
                        "dpto_desc," +
                        "secc_desc," +
                        "flia_desc," +
                        "grup_desc," +
                        "area_desc," +
                        "winvd_cant_inv," +
                        "estado) VALUES ('"+
                        rs.getInt("ARDE_SUC")               +"','"+
                        rs.getInt("winvd_nro_inv")          +"','"+
                        rs.getString("winvd_art")           +"','"+
                        rs.getString("ART_DESC")            +"','"+
                        rs.getString("winvd_lote")          +"','"+
                        rs.getString("winvd_fec_vto")       +"','"+
                        rs.getString("winvd_area")          +"','"+
                        rs.getString("winvd_dpto")          +"','"+
                        rs.getString("winvd_secc")          +"','"+
                        rs.getString("winvd_flia")          +"','"+
                        rs.getString("winvd_grupo")         +"','"+
                        rs.getString("winvd_cant_act")      +"','"+
                        rs.getString("winve_fec")           +"','"+
                        rs.getString("dpto_desc")           +"','"+
                        rs.getString("secc_desc")           +"','"+
                        rs.getString("flia_desc")           +"','"+
                        rs.getString("grup_desc")           +"','"+
                        rs.getString("area_desc")           +"'," +
                        "'0'," +
                        "'A') ");
                //ESTADO PENDIENTE A INVENTARIAR.
            }


              prodialog.setProgress(i);
            i++;
        }
        prodialog.dismiss();
        }
        catch (Exception e){
        String error=e.toString();
            prodialog.dismiss();
        }


    }
    class AsyncImportador extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            test();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}