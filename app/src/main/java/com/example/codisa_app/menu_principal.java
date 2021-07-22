package com.example.codisa_app;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.spec.ECField;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Utilidades.Stkw002List;
import Utilidades.controles;
import Utilidades.variables;
import maes.tech.intentanim.CustomIntent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class menu_principal extends AppCompatActivity {
    public static ProgressDialog prodialog,ProDialogExport;
    public  static TextView txt_total;
    CardView tomasGen;
    AlertDialog.Builder builder;
    AlertDialog ad;
    String mensajeRespuesta="";
    int error_importador=1;

    static int   ContProgressBarImportador=0;
      String mensajeImporError="";
    public void onBackPressed()  {
        Utilidades.controles.volver_atras(this,this, login.class,"¿Desea salir de la aplicación?",3);
    }
    @SuppressLint("WrongConstant")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollmenu);
        txt_total=findViewById(R.id.txttotalpendiente);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //bellow setSupportActionBar(toolbar);
        getSupportActionBar().setCustomView(R.layout.customactionbar);
        TextView txt1 = (TextView) getSupportActionBar().getCustomView().findViewById( R.id.action_bar_title);
        TextView txt2 = (TextView) getSupportActionBar().getCustomView().findViewById( R.id.action_bar_title2);
        txt2.setVisibility(View.VISIBLE);
        txt1.setText("Usuario:       "+variables.NOMBRE_LOGIN);
        txt2.setText("Sucursal:     "+variables.DESCRIPCION_SUCURSAL_LOGIN);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorlogin)));


        controles.conexion_sqlite(this);
        controles.ConsultarPendientesExportar();
        controles.context_menuPrincipal=this;
        tomasGen=findViewById(R.id.tomasGen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String[] array_opciones=variables.contenedor_menu.split(",");

        for(int i=0; i<array_opciones.length; i++)
        {
            String id=array_opciones[i];
            if(id.equals("STKW001")){
                tomasGen.setVisibility(View.VISIBLE);
            }
            int ID_BOTONES = getResources().getIdentifier(array_opciones[i], "id", getPackageName());
            CardView stock = (findViewById(ID_BOTONES));

            stock.setVisibility(View.VISIBLE);
        }

    }

    public void OnclickIrStkw002(View v){
        variables.tipoListaStkw002=1;
        variables.tipoStkw002=2;
        Intent i=new Intent(this,lista_stkw002_inv.class);
        startActivity(i);
        CustomIntent.customType(menu_principal.this,"left-to-right");
    }

    public void OnclickIrStkw001(View v){
        builder = new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_danger));
        builder.setTitle("¡Atención!");
        builder.setMessage("Seleccione el tipo de toma que desea generar.");
        builder.setPositiveButton("Manual", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        variables.titulo_stkw001="TOMA MANUAL";
                        variables.tipo_stkw001=1;
                        variables.tipo_stkw001_insert="M";

                        Intent intent = new Intent(menu_principal.this, stkw001.class);
                        finish();
                        startActivity(intent);
                        CustomIntent.customType(menu_principal.this,"left-to-right");

                    }
                });
        builder.setNeutralButton("Por criterio de selección",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        variables.titulo_stkw001="TOMA POR CRITERIO DE SELECCION";
                        variables.tipo_stkw001=2;
                        variables.tipo_stkw001_insert="C";
                        Intent intent = new Intent(menu_principal.this, stkw001.class);
                        finish();
                        startActivity(intent);
                        CustomIntent.customType(menu_principal.this,"left-to-right");

                    }
                });
        ad = builder.show();
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.azul_claro));
        ad.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.azul_claro));
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        ad.getButton(AlertDialog.BUTTON_NEUTRAL).setAllCaps(false);


    }

    public void OnclickIrStkw001Cancelacion(View v){

        Intent intent = new Intent(this, lista_stkw001_inv.class);
        finish();
        startActivity(intent);
        CustomIntent.customType(menu_principal.this,"left-to-right");

    }

    public void OnclickExportar( View v){
    try {

        controles.ExportarStkw002();

    }
    catch (Exception e){
        new androidx.appcompat.app.AlertDialog.Builder( this)
                .setTitle("INFORME!!!")
                .setMessage(e.toString()).show();

    }
    }

    public void OnclickSincronizarDatos(View v){

        final HiloSincronizar task = new HiloSincronizar();
        task.execute();
      //ImportarTomas();


    }

    public   class HiloSincronizar extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prodialog = ProgressDialog.show(menu_principal.this, "PROCESANDO", "ESPERE...", true);
        }
        @Override
        protected Void doInBackground(Void... params) {
           try {
               error_importador=1;
               controles.connect = controles.conexion.Connections();
               Statement stmt = controles.connect.createStatement();
               ResultSet rs = stmt.executeQuery(
                       "        select count(*) as contador " +
                               "        from " +
                               "        WEB_INVENTARIO a " +
                               "        inner join WEB_INVENTARIO_det b on a.winve_numero=b.winvd_nro_inv   " +
                               "    where a.WINVE_ESTADO_WEB='A' and a.winve_empr="+variables.ID_SUCURSAL_LOGIN+"");

               while (rs.next())
               {
                   ContProgressBarImportador=rs.getInt("contador");
               }
               rs.close();
               error_importador=0;


           }
           catch (Exception e){

               mensajeImporError=e.getMessage();

           }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            prodialog.dismiss();
            if (error_importador==0){//SI NO HAY ERRORES

                builder = new AlertDialog.Builder(menu_principal.this);
                builder.setIcon(getResources().getDrawable(R.drawable.ic_danger));
                builder.setTitle("Sincronización de tomas.");
                builder.setMessage("¿Desea importar las tomas disponibles?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prodialog =  new ProgressDialog( menu_principal.this);
                        prodialog.setMax(ContProgressBarImportador);
                        LayerDrawable progressBarDrawable = new LayerDrawable(
                                new Drawable[]{
                                        new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                new int[]{Color.parseColor("black"),Color.parseColor("black")}),
                                        new ClipDrawable(
                                                new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                        new int[]{Color.parseColor("blue"),Color.parseColor("blue")}),
                                                Gravity.START,
                                                ClipDrawable.HORIZONTAL),
                                        new ClipDrawable(
                                                new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                                        new int[]{Color.parseColor("blue"),Color.parseColor("blue")}),
                                                Gravity.START,
                                                ClipDrawable.HORIZONTAL)
                                });
                        progressBarDrawable.setId(0,android.R.id.background);
                        progressBarDrawable.setId(1,android.R.id.secondaryProgress);
                        progressBarDrawable.setId(2,android.R.id.progress);
                        prodialog.setTitle("Sincronizando tomas.");
                        prodialog.setMessage("Favor espere...");
                        prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        prodialog.setProgressDrawable(progressBarDrawable);
                        prodialog.show();

                        prodialog.setCanceledOnTouchOutside(false);
                        prodialog.setCancelable(false);
                        final AsyncImportador task = new  AsyncImportador();
                        task.execute();

                    }
                });
                builder.setNegativeButton("No",null);
                ad = builder.show();

                ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.azul_claro));
                ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.azul_claro));
                ad.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                ad.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);

            }

            else {
                new androidx.appcompat.app.AlertDialog.Builder(menu_principal.this)
                        .setTitle("ATENCION!!!")
                        .setCancelable(false)
                        .setMessage(mensajeImporError)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()  {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).show();
            }



        }
    }

    private void InsertarSqliteToma(){
        try {
            SQLiteDatabase db1= controles.conSqlite.getReadableDatabase();
            db1.execSQL("delete from STKW002INV  WHERE estado IN ('A','C','E')");
            db1.close();
            controles.connect = controles.conexion.Connections();
            Statement stmt = controles.connect.createStatement();

       /*     ResultSet rs = stmt.executeQuery(
                    "       SELECT  " +
                            "       distinct  b.winvd_art,  a.ARDE_SUC, b.winvd_nro_inv, a.ART_DESC,b.winvd_lote," +
                            "       b.winvd_fec_vto,b.winvd_area,b.winvd_dpto,b.winvd_secc,b.winvd_flia,b.winvd_grupo," +
                            "       b.winvd_cant_act,c.winve_fec,dpto_desc,secc_desc,flia_desc,grup_desc,area_desc," +
                            "       sugr_codigo,b.winvd_secu, case c.winve_tipo_toma when 'C' then 'CRITERIO' ELSE 'MANUAL' END AS tipo_toma," +
                            "       c.winve_login,  'SI'  AS winvd_consolidado , " +
                            "        case when c.winve_grupo IS NULL and  c.winve_grupo_parcial IS NULL then 'TODOS'    " +
                            "        WHEN c.winve_grupo_parcial IS NOT NULL THEN 'PARCIALES' ELSE grup_desc END AS desc_grupo_parcial," +
                            "       case when c.winve_flia is null then 'TODAS' else a.flia_desc end as desc_familia                    " +
                            "   FROM    " +
                            "       V_WEB_ARTICULOS_CLASIFICACION  a    " +
                            "       inner join WEB_INVENTARIO_det b on   a.ART_CODIGO=b.winvd_art        " +
                            "       and a.SECC_CODIGO=b.winvd_secc                           " +
                            "       inner join  WEB_INVENTARIO c on b.winvd_nro_inv=c.winve_numero  And c.winve_dep=a.ARDE_DEP  " +
                            "       and c.winve_area=a.AREA_CODIGO                             " +
                            "       and c.winve_suc=a.ARDE_SUC   and c.winve_secc=a.SECC_CODIGO   " +
                            "   where  c.winve_empr="+variables.ID_SUCURSAL_LOGIN+"   and a.ARDE_SUC=1 AND WINVE_ESTADO_WEB='A' ");
            */

            ResultSet rs = stmt.executeQuery(
                    "      SELECT  "+
                        "     DISTINCT b.winvd_art,  a.ARDE_SUC, b.winvd_nro_inv, a.ART_DESC,'' AS winvd_lote,"+
                    "   '' AS winvd_fec_vto,b.winvd_area,b.winvd_dpto,b.winvd_secc,b.winvd_flia,b.winvd_grupo,"+
                    "   '' AS winvd_cant_act,c.winve_fec,dpto_desc,secc_desc,flia_desc,grup_desc,area_desc,"+
                    "   sugr_codigo,'' AS winvd_secu, case c.winve_tipo_toma when 'C' then 'CRITERIO' ELSE 'MANUAL' END AS tipo_toma,"+
                    "   c.winve_login,  ''  AS winvd_consolidado ,"+
                    "     case when c.winve_grupo IS NULL and  c.winve_grupo_parcial IS NULL then 'TODOS'"+
                    "     WHEN c.winve_grupo_parcial IS NOT NULL THEN 'PARCIALES' ELSE grup_desc END AS desc_grupo_parcial,"+
                    "   case when c.winve_flia is null then 'TODAS' else a.flia_desc end as desc_familia,c.winve_dep,c.winve_suc"+
                    "    FROM"+
                    "   V_WEB_ARTICULOS_CLASIFICACION  a"+
                    "   inner join WEB_INVENTARIO_det b on   a.ART_CODIGO=b.winvd_art"+
                    "   and a.SECC_CODIGO=b.winvd_secc"+
                    "    inner join  WEB_INVENTARIO c on b.winvd_nro_inv=c.winve_numero  And c.winve_dep=a.ARDE_DEP"+
                    "   and c.winve_area=a.AREA_CODIGO"+
                    "   and c.winve_suc=a.ARDE_SUC   and c.winve_secc=a.SECC_CODIGO"+
                    "   where  c.winve_empr=1   and a.ARDE_SUC="+variables.ID_SUCURSAL_LOGIN+" AND WINVE_ESTADO_WEB='A'"+
                    "    GROUP BY WINVD_ART,ARDE_SUC,a.ART_DESC,WINVD_NRO_INV,"+
                    "   WINVD_AREA,WINVD_DPTO,WINVD_SECC,WINVD_FLIA,WINVD_GRUPO,SUGR_CODIGO,WINVE_LOGIN,c.winve_fec,dpto_desc"+
                    "   ,secc_desc,flia_desc,grup_desc,area_desc,b.winvd_secu, c.winve_tipo_toma,c.winve_grupo,c.winve_grupo_parcial,"+
                    "   c.winve_flia,winve_dep, winve_suc");


            int i=1;
            int contadorMensaje=0;
        while (rs.next())
        {// SI SE QUIERE VOLVER A IMPORTAR UNA TOMA, QUE YA SE ENCUENTRA INVENTARIADO PERO CON PENDIENTE DE EXPORTACION,
            // ENTONCES NO HACE INSERT AL SQLITE.
            contadorMensaje++;
            SQLiteDatabase db_consulta= controles.conSqlite.getReadableDatabase();
            Cursor cursor=db_consulta.rawQuery("select * from STKW002INV where  winvd_nro_inv ='"+rs.getInt("winvd_nro_inv")+"' and estado='P'" ,null);
            if (cursor.moveToNext())
            {

            }
            else
            {
                String decripcionArt=  rs.getString("ART_DESC").replaceAll("'","");
                SQLiteDatabase dbdbSTKW002INV=controles.conSqlite.getReadableDatabase();
                dbdbSTKW002INV.execSQL(" INSERT INTO  STKW002INV (" +
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
                "winvd_subgr," +
                "winvd_secu," +
                "estado," +
                "tipo_toma, " +
                "winve_login," +
                "winvd_consolidado," +
                "desc_grupo_parcial," +
                    "desc_familia,winve_dep, winve_suc) " +
                "VALUES ('"+
                            rs.getInt("ARDE_SUC")               +"','"+
                            rs.getInt("winvd_nro_inv")          +"','"+
                            rs.getString("winvd_art")           +"','"+
                            decripcionArt           +"','"+
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
                            rs.getString("area_desc")           +"','"+
                            "0','"                                         +
                            rs.getString("sugr_codigo")         +"','" +
                            rs.getString("winvd_secu")          +"','" +
                            "A','"+rs.getString("tipo_toma")    +"','"
                            +rs.getString("winve_login")   +"','"
                            +rs.getString("winvd_consolidado")+"','"
                        +rs.getString("desc_grupo_parcial")+"','"
                        +rs.getString("desc_familia")+"','"
                        +rs.getString("winve_dep")+"','"
                            +rs.getString("winve_suc")+"'" +
                        ") "); //ESTADO PENDIENTE A INVENTARIAR.
                dbdbSTKW002INV.close();
            }
            db_consulta.close();
            prodialog.setProgress(i);
            i++;
        }
            if(contadorMensaje==0){
                mensajeRespuesta="No se encontraron registros por importar.";
            }
            else {
                mensajeRespuesta="Datos sincronizados correctamente.";

            }

        rs.close();
        prodialog.dismiss();
        }
        catch (Exception e)
        {
            mensajeRespuesta=e.toString();
           // Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            prodialog.dismiss();
        }
    }

    class AsyncImportador extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            InsertarSqliteToma();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


                    builder = new AlertDialog.Builder(menu_principal.this);
            builder.setIcon(getResources().getDrawable(R.drawable.ic_danger));
            builder.setTitle("¡Atención!");
            builder.setMessage(mensajeRespuesta);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad = builder.show();
            ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.azul_claro));
            ad.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);

            super.onPostExecute(result);
        }
    }
}