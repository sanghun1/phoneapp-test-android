package com.cos.phoneapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";

    private ConstraintLayout mainLayout;
    private PhoneAdapter phoneAdapter;
    private PhoneService phoneService;
    private RecyclerView rvPhoneItem;
    private FloatingActionButton fab;

    private List<Phone> phones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        Call<CMRespDto<List<Phone>>> call = phoneService.findAll();
        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();
                phones = cmRespDto.getData();

                Log.d(TAG, "onResponse: 응답 받은 데이터" + phones);

                LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                rvPhoneItem.setLayoutManager(manager);

                phoneAdapter = new PhoneAdapter(phones, MainActivity.this);
                rvPhoneItem.setAdapter(phoneAdapter);
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onFailure: findAll() 실패");
            }
        });

        fab.setOnClickListener(v -> {
            add();
        });

    }

    private void init(){
        mainLayout = findViewById(R.id.main_layout);
        rvPhoneItem = findViewById(R.id.rv_phone);
        fab = findViewById(R.id.fab_save);
        phoneService = PhoneService.retrofit.create(PhoneService.class);
    }
    public void add() {

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.phone_add_update, null);

        EditText etName = view.findViewById(R.id.text_name);
        EditText etTel = view.findViewById(R.id.text_tel);

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("연락처 등록");
        dlg.setView(view);
        dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                create(etName.getText().toString(), etTel.getText().toString());
                Snackbar.make(mainLayout, etName.getText().toString() + "님의 연락처가 등록되었습니다.",1000).show();
            }
        });
        dlg.setNegativeButton("닫기", null);
        dlg.show();

    }

    public void create(String name, String tel) {
        Phone phone = new Phone();
        phone.setName(name);
        phone.setTel(tel);

        Call<CMRespDto<Phone>> call = phoneService.save(phone);

        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<Phone> cmRespDto = response.body();
                Phone phone = cmRespDto.getData();
                phones.add(phone);
                phoneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 연락처 등록 실패");
                Snackbar.make(mainLayout, "연락처 등록을 실패하였습니다.",1000).show();
            }
        });
    }

    public void edit(int position, Phone phone) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.phone_add_update, null);

        EditText etName = view.findViewById(R.id.text_name);
        EditText etTel = view.findViewById(R.id.text_tel);

        etName.setText(phone.getName());
        etTel.setText(phone.getTel());

        Long id = phone.getId();

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("연락처 수정");
        dlg.setView(view);

        dlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                update(id, position, etName.getText().toString(), etTel.getText().toString());
                Snackbar.make(mainLayout, etName.getText().toString() + "님의 연락처가 수정되었습니다.",1000).show();
            }
        });

        dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(id, position);
                Snackbar.make(mainLayout, etName.getText().toString() + "님의 연락처가 삭제되었습니다.",1000).show();
            }
        });
        dlg.show();
    }

    public void update(Long id, int position, String name, String tel) {
        Phone phone = new Phone(null,name,tel);
        Call<CMRespDto<Phone>> call = phoneService.update(id, phone);

        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<Phone> cmRespDto = response.body();
                Phone phone = cmRespDto.getData();
                phones.set(position, phone);
                phoneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 연락처 수정 실패");
                Snackbar.make(mainLayout,"연락처 수정을 실패하였습니다.",1000).show();
            }
        });
    }

    public void delete(Long id, int position){
        Call<CMRespDto<Phone>> call = phoneService.delete(id);

        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<Phone> cmRespDto = response.body();
                Log.d(TAG, "onResponse: 삭제" + cmRespDto);
                phones.remove(position);
                phoneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 연락처 삭제 실패");
                Snackbar.make(mainLayout,"연락처 삭제를 실패하였습니다.",1000).show();
            }
        });

    }
}