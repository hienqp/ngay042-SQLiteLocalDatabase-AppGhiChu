package com.hienqp.appghichu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Database database;
    ListView listViewCongViec;
    ArrayList<CongViec> arrayListCongViec;
    CongViecAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewCongViec = (ListView) findViewById(R.id.listView_cong_viec);
        arrayListCongViec = new ArrayList<>();

        adapter = new CongViecAdapter(MainActivity.this, R.layout.layout_dong_cong_viec, arrayListCongViec);
        listViewCongViec.setAdapter(adapter);

        createDatabase();

        createTable();

        RefreshListView();
    }

    private void createDatabase() {
        // khởi tạo database với tên database là ghichu.sqlite
        database = new Database(MainActivity.this, "ghichu.sqlite", null, 1);
    }

    private void createTable() {
        // khởi tạo table CongViec nếu chưa tồn tại có 2 cột (lúc này chưa có dữ liệu):
        // Id kiểu INTEGER là khóa chính và tự động tăng dần
        // TenCV kiểu VARCHAR (có thể chỉ định chiều dài cho TenCV ví dụ: VARCHAR(200))
        database.QueryData("CREATE TABLE IF NOT EXISTS CongViec(Id INTEGER PRIMARY KEY AUTOINCREMENT, TenCV VARCHAR)");
    }

    private void insertData(String tenCV) {
        // INSERT data INTO table-name,
        // do Id ta đặt tự động tăng dần,
        // nên VALUES INSERT vào database không cần truyền Id nhưng vẫn truyền null để đủ tham số
        database.QueryData("INSERT INTO CongViec VALUES(null, '" + tenCV + "')");
    }

    private void updateData(int id, String tenMoi) {
        // lưu ý điều kiện WHERE (vị trí), nếu không chỉ rõ vị trí thì
        // sẽ cập nhật cho toàn bộ các dòng trong table với cùng value
        database.QueryData("UPDATE CongViec SET TenCV = '" + tenMoi + "' WHERE Id = '" + id + "'");
    }

    private void deleteData(int id) {
        database.QueryData("DELETE FROM CongViec WHERE Id = '" + id + "'");
    }

    private Cursor getCursor(String tableName) {
        return database.GetData("SELECT * FROM " + tableName);
    }

    // phương thức SELECT vào table CongViec, lấy ra Cursor, duyệt qua từng row trong table lấy data
    // lấy data từ table đưa vào List trống, đổ ra ListView data của List hiện tại (hiển thị mới nhất)
    void RefreshListView() {
        // SELECT * FROM table-name
        String tableName = "CongViec";
        Cursor dataCongViec = getCursor(tableName);

        // làm trống arrayList trước khi thêm dữ liệu mới từ database để đổ ra ListView
        arrayListCongViec.clear();

        // duyệt con trỏ qua từng dòng trong table
        while (dataCongViec.moveToNext()) {
            // ở vị trí con trỏ hiện tại: lấy dữ liệu có kiểu tương ứng tại cột Id (cột 0)
            int id = dataCongViec.getInt(0);

            // ở vị trí con trỏ hiện tại: lấy dữ liệu có kiểu tương ứng tại cột TenCV (cột 1)
            String ten = dataCongViec.getString(1);

            // add phần tử vào ArrayList
            arrayListCongViec.add(new CongViec(id, ten));
//            Toast.makeText(MainActivity.this, ten, Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_menu_add_cong_viec, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_button_add) {
            DialogAddCongViec();
        }

        return super.onOptionsItemSelected(item);
    }

    private void DialogAddCongViec() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // bỏ title trắng mặc định
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_dialog_add_cong_viec);

        EditText editTextAddCongViec = (EditText) dialog.findViewById(R.id.editText_add_cong_viec);
        Button buttonAddCongViec = (Button) dialog.findViewById(R.id.button_add_cong_viec);
        Button buttonCancelAddCongViec = (Button) dialog.findViewById(R.id.button_cancel_add_cong_viec);

        buttonAddCongViec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenCV = editTextAddCongViec.getText().toString().trim();
                if (tenCV.equals("")) {
                    ThongBaoToast("Vui lòng nhập tên Công Việc");
                } else {
                    // add Cong Viec
                    insertData(tenCV);

                    ThongBaoToast("Đã thêm Công việc mới");
                    dialog.dismiss();

                    RefreshListView();
                }
            }
        });

        buttonCancelAddCongViec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void DialogUpdateCongViec(int id, String tenCV) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // bỏ title trắng mặc định
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_dialog_update_cong_viec);

        EditText editTextUpdateTenCongViec = (EditText) dialog.findViewById(R.id.dialog_editText_update_ten_cong_viec);
        editTextUpdateTenCongViec.setText(tenCV);

        Button buttonCancelUpdateCongViec = (Button) dialog.findViewById(R.id.dialog_button_cancel_update_ten_cong_viec);
        Button buttonConfirmUpdateCongViec = (Button) dialog.findViewById(R.id.dialog_button_confirm_update_ten_cong_viec);

        buttonConfirmUpdateCongViec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenMoi = editTextUpdateTenCongViec.getText().toString().trim();

                if (tenMoi.equals("") || tenMoi.equals(tenCV)) {
                    ThongBaoToast("Bạn chưa cập nhật dữ liệu");
                } else {
                    updateData(id, tenMoi);

                    ThongBaoToast("Đã cập nhật xong");
                    dialog.dismiss();
                    RefreshListView();
                }
            }
        });

        buttonCancelUpdateCongViec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void DialogDeleteCongViec(int id, String tenCV) {
        AlertDialog.Builder dialogDeleteCongViec = new AlertDialog.Builder(MainActivity.this);
        dialogDeleteCongViec.setMessage("Bạn có muốn xóa công việc [" + tenCV + "] này hay không ?");

        dialogDeleteCongViec.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData(id);
                ThongBaoToast("Đã xóa thành công");
                RefreshListView();
            }
        });

        dialogDeleteCongViec.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogDeleteCongViec.show();
    }

    private void ThongBaoToast(String toast) {
        Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
    }
}