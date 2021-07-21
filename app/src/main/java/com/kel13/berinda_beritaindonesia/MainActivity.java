 package com.kel13.berinda_beritaindonesia;

 import android.app.ProgressDialog;
 import android.app.SearchManager;
 import android.content.Context;
 import android.content.Intent;
 import android.os.Bundle;
 import androidx.recyclerview.widget.LinearLayoutManager;
 import androidx.recyclerview.widget.RecyclerView;
 import com.google.android.material.navigation.NavigationView;
 import androidx.core.view.GravityCompat;
 import androidx.drawerlayout.widget.DrawerLayout;
 import androidx.appcompat.app.ActionBarDrawerToggle;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.widget.SearchView;
 import android.widget.Toast;

 import com.kel13.berinda_beritaindonesia.api.ApiService;
 import com.kel13.berinda_beritaindonesia.api.Server;
 import com.kel13.berinda_beritaindonesia.adapter.NewsAdapter;
 import com.kel13.berinda_beritaindonesia.kategori.Business;
 import com.kel13.berinda_beritaindonesia.kategori.Entertainment;
 import com.kel13.berinda_beritaindonesia.kategori.Health;
 import com.kel13.berinda_beritaindonesia.kategori.Science;
 import com.kel13.berinda_beritaindonesia.kategori.Sports;
 import com.kel13.berinda_beritaindonesia.kategori.Technology;
 import com.kel13.berinda_beritaindonesia.entity.News;
 import com.kel13.berinda_beritaindonesia.entity.ResponseNews;

 import java.util.ArrayList;
 import java.util.List;

 import retrofit2.Call;
 import retrofit2.Callback;
 import retrofit2.Response;

 public class MainActivity extends AppCompatActivity
         implements NavigationView.OnNavigationItemSelectedListener {

     private RecyclerView news;
     private NewsAdapter adapter;
     List<News> list = new ArrayList<>();
     ProgressDialog loading;
     ApiService api;
     private static final int TIME_LIMIT = 1800;
     private static long backPressed;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         news = findViewById(R.id.news);
         api = Server.getApiService();
         adapter = new NewsAdapter(MainActivity.this, list);

         news.setHasFixedSize(true);
         news.setLayoutManager(new LinearLayoutManager(MainActivity.this));
         news.setAdapter(adapter);
         refresh("");

         Toolbar toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);

         DrawerLayout drawer = findViewById(R.id.drawer_layout);
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                 this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
         drawer.addDrawerListener(toggle);
         toggle.syncState();

         NavigationView navigationView = findViewById(R.id.nav_view);
         navigationView.setNavigationItemSelectedListener(this);
     }

     public void refresh(final String keyword) {
         loading = new ProgressDialog(MainActivity.this);
         loading.setCancelable(false);
         loading.setMessage("Loading...");
         Call<ResponseNews> call;
         showDialog();
         if(keyword.length() > 0){
             call = api.getNewsSearch(keyword, "id", "publishedAt", BuildConfig.NEWS_API_TOKEN);
         }else{
             call = api.getListAllNews("id", BuildConfig.NEWS_API_TOKEN);
         }
         call.enqueue(new Callback<ResponseNews>() {
             @Override
             public void onResponse(Call<ResponseNews> call, Response<ResponseNews> response) {
                 if (response.isSuccessful()){
                     hideDialog();
                     list = response.body().getNewsList();
                     news.setAdapter(new NewsAdapter(MainActivity.this, list));
                     adapter.notifyDataSetChanged();
                 } else {
                     hideDialog();
                     Toast.makeText(MainActivity.this, "Gagal mengambil data !", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<ResponseNews> call, Throwable t) {
                 hideDialog();
                 Toast.makeText(MainActivity.this, "Gagal menyambung ke internet !", Toast.LENGTH_SHORT).show();
             }
         });
     }

     private void showDialog() {
         if (!loading.isShowing())
             loading.show();
     }

     private void hideDialog() {
         if (loading.isShowing())
             loading.dismiss();
     }

     @Override
     public void onBackPressed() {
         DrawerLayout drawer = findViewById(R.id.drawer_layout);
         if (drawer.isDrawerOpen(GravityCompat.START)) {
             drawer.closeDrawer(GravityCompat.START);
         } else {

             if (TIME_LIMIT + backPressed > System.currentTimeMillis()) {
                 // super.onBackPressed();
                 moveTaskToBack(true);
             } else {
                 Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
             }
             backPressed = System.currentTimeMillis();

         }
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.main, menu);
         SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
         MenuItem searchMenuItem = menu.findItem(R.id.action_search);

         searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
         searchView.setQueryHint("Search Latest News...");
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
             @Override
             public boolean onQueryTextSubmit(String query){
                 if (query.length() > 2){
                     refresh(query);
                 }
                 return false;
             }
             @Override
             public boolean onQueryTextChange(String newText){
                 refresh(newText);
                 return false;
             }
         });

         searchMenuItem.getIcon().setVisible(false, false);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

         return super.onOptionsItemSelected(item);
     }

     @SuppressWarnings("StatementWithEmptyBody")
     @Override
     public boolean onNavigationItemSelected(MenuItem item) {
         // Handle navigation view item clicks here.
         int id = item.getItemId();

         if (id == R.id.business) {
             Intent bus = new Intent(MainActivity.this, Business.class);
             startActivity(bus);
             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         } else if (id == R.id.entertainment) {
             Intent enter = new Intent(MainActivity.this, Entertainment.class);
             startActivity(enter);
             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         } else if (id == R.id.health) {
             Intent heal = new Intent(MainActivity.this, Health.class);
             startActivity(heal);
             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         } else if (id == R.id.science) {
             Intent scien = new Intent(MainActivity.this, Science.class);
             startActivity(scien);
             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         } else if (id == R.id.sports) {
             Intent sport = new Intent(MainActivity.this, Sports.class);
             startActivity(sport);
             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         } else if (id == R.id.technology) {
             Intent tech = new Intent(MainActivity.this, Technology.class);
             startActivity(tech);
             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         }

         DrawerLayout drawer = findViewById(R.id.drawer_layout);
         drawer.closeDrawer(GravityCompat.START);
         return true;
     }
 }
