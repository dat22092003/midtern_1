package com.example.midtern_1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText edt_chieucao,edt_cannang;
    private Button btn_tinh,btn_reset;
    private TextView tv_chiso,tv_nhomnguoi;
    private CheckBox cb_luu;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        edt_chieucao = findViewById(R.id.edt_chieucao);
        edt_cannang = findViewById(R.id.edt_cannang);
        btn_tinh = findViewById(R.id.btn_tinh);
        btn_reset = findViewById(R.id.btn_reset);
        tv_chiso = findViewById(R.id.tv_chiso);
        tv_nhomnguoi = findViewById(R.id.tv_nhomnguoi);
        cb_luu = findViewById(R.id.cb_luu);
        sharedPreferences = getSharedPreferences("BMIData", MODE_PRIVATE);
        load_thongtin();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_chieucao.setText("");
                edt_cannang.setText("");
                tv_chiso.setText("Chỉ số BMI của bạn là : ");
                tv_nhomnguoi.setText("Bạn thuộc nhóm người : ");
                cb_luu.setChecked(false);
            }
        });

        btn_tinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chieucao = edt_chieucao.getText().toString();
                String canang = edt_cannang.getText().toString();
                if (!chieucao.isEmpty() && !canang.isEmpty()) {
                    double ChieuCao = Double.parseDouble(chieucao);
                    double CanNang = Double.parseDouble(canang);
                    double bmi = tinh_bmi(ChieuCao,CanNang);
                    String NhomNguoi = phanLoaiBMI(bmi);
                    String bmiText = String.format("Chỉ số BMI của bạn là : %.3f", bmi);
                    SpannableString spannableBmiString = color_text(bmiText, String.format("%.3f", bmi), R.color.red);
                    tv_chiso.setText(spannableBmiString);

                    String nhomNguoiText = String.format("Bạn thuộc nhóm người : %s", NhomNguoi);
                    SpannableString spannableNhomNguoiString =color_text(nhomNguoiText, NhomNguoi, R.color.red);
                    tv_nhomnguoi.setText(spannableNhomNguoiString);


                    if(cb_luu.isChecked())
                    {
                        luu_sharedPreferences(ChieuCao,CanNang,bmi,NhomNguoi);

                        Toast.makeText(MainActivity.this, "Thông tin đã được lưu", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
        private double tinh_bmi ( double chieuCao , double canNang)
        {
            return canNang / (chieuCao * chieuCao);
        }
    private String phanLoaiBMI(double bmi) {
        if (bmi < 18.5) {
            return "Người gầy";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            return "Bình thường";
        } else if (bmi >= 25 && bmi < 29.9) {
            return "Thừa cân";
        } else {
            return "Béo phì";
        }
    }

    private void luu_sharedPreferences(double chieuCao, double canNang, double bmi, String nhomNguoi) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("chieuCao", (float) chieuCao);
        editor.putFloat("canNang", (float) canNang);
        editor.putFloat("bmi", (float) bmi);
        editor.putString("nhomNguoi", nhomNguoi);
        editor.putBoolean("luuThongTin", cb_luu.isChecked());
        editor.apply();
    }
    private void load_thongtin() {
        if (sharedPreferences.getBoolean("luuThongTin", false)) {
            float chieuCao = sharedPreferences.getFloat("chieuCao", 0);
            float canNang = sharedPreferences.getFloat("canNang", 0);
            float bmi = sharedPreferences.getFloat("bmi", 0);
            String nhomNguoi = sharedPreferences.getString("nhomNguoi", "");
            edt_chieucao.setText(String.valueOf(chieuCao));
            edt_cannang.setText(String.valueOf(canNang));
            tv_chiso.setText(String.format("Chỉ số BMI của bạn là : %.2f", bmi));
            tv_nhomnguoi.setText(String.format("Bạn thuộc nhóm người : %s", nhomNguoi));
            cb_luu.setChecked(true);
        }
    }
    private void xoa_sharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        edt_chieucao.setText("");
        edt_cannang.setText("");
        tv_chiso.setText("Chỉ số BMI của bạn là : ");
        tv_nhomnguoi.setText("Bạn thuộc nhóm người : ");
        cb_luu.setChecked(false);
    }
    private SpannableString color_text(String text, String colorText, int colorResId) {
        SpannableString spannableString = new SpannableString(text);

        int start = text.indexOf(colorText);
        int end = start + colorText.length();

        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(colorResId)),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return spannableString;
    }
    protected void onPause() {
        super.onPause();
        if (!cb_luu.isChecked()) {
            xoa_sharedPreferences();
        }
    }
    }

