# 1. SQLite Local Database (khoapham.vn) <a id="1"></a>
________________________________________________________________________________________________________________________
- xây dựng 1 ứng dụng quản lý các ghi ghú:
  - hiển thị trên ListView
  - mỗi dòng 
    - có nội dung ghi chú
    - 1 Button delete ghi chú
    - 1 Button update ghi chú

## 1.1. Tạo Class Quản Lý SQLite Local Database <a id="1.1"></a>
________________________________________________________________________________________________________________________
- để thao tác với SQLite ta phải tạo 1 class extends từ ``SQLiteOpenHelper``.
- sau khi tạo class ``Database.java`` extends từ ``SQLiteOpenHelper``, trình biên dịch yêu cầu
  - override 2 phương thức ``onCreate`` và ``onUpgrade()``
  - generate 1 constructor mặc định
- sau khi thực hiện đầy đủ các bước trên, ta thực hiện bước kế tiếp là tạo các phương thức để thao tác với SQLite mà không
dùng ``onCreate`` và ``onUpgrade()``
- khai báo phương thức truy vấn không trả kết quả mà chỉ thực thi (CREATE, INSERT, UPDATE, DELETE, ....)
```java
// phương thức truy vấn không trả về kết quả (chỉ thực thi): CREATE, INSERT, UPDATE, DELETE
// phương thức này sẽ nhận tham số kiểu String là câu lệnh SQL
public void QueryData(String sql) {
    SQLiteDatabase sqLiteDatabase = getWritableDatabase();
    sqLiteDatabase.execSQL(sql);
}
```
- khai báo phương thức truy vấn đồng thời lấy ra kết quả là 1 con trỏ Cursor để duyệt data trong table (SELECT)
```java
// phương thức truy vấn đồng thời lấy ra kết quả: SELECT
// phương thức này sẽ nhận tham số kiểu String là câu lệnh SQL
// kết quả trả về dạng con trỏ Cursor
public Cursor GetData(String sql) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    return sqLiteDatabase.rawQuery(sql, null);
}
```
- khai báo 1 Constructor mặc định
```java
// Constructor
public Database(
        @Nullable Context context,
        @Nullable String name,
        @Nullable SQLiteDatabase.CursorFactory factory,
        int version) {
    super(context, name, factory, version);
}
```
- class ``Database.java``
```java
package com.hienqp.appghichu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    // Constructor
    public Database(
            @Nullable Context context,
            @Nullable String name,
            @Nullable SQLiteDatabase.CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    // phương thức truy vấn không trả về kết quả (chỉ thực thi): CREATE, INSERT, UPDATE, DELETE
    // phương thức này sẽ nhận tham số kiểu String là câu lệnh SQL
    public void QueryData(String sql) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }

    // phương thức truy vấn đồng thời lấy ra kết quả: SELECT
    // phương thức này sẽ nhận tham số kiểu String là câu lệnh SQL
    // kết quả trả về dạng con trỏ Cursor
    public Cursor GetData(String sql) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
```

## 1.2. Tạo Class Đối Tượng Công Việc <a id="1.2"></a>
________________________________________________________________________________________________________________________
- ListView chỉ hiển thị tên công việc, vì vậy đối tượng Công Việc chỉ có 2 thuộc tính ID và TenCongViec.
- 2 ImageView Delete và Edit chỉ dùng để bắt sự kiện, không thuộc quản lý của database
```java
package com.hienqp.appghichu;

public class CongViec {
    private int mIdCV;
    private String mtenCV;

    public CongViec(int mIdCV, String mtenCV) {
        this.mIdCV = mIdCV;
        this.mtenCV = mtenCV;
    }

    public int getmIdCV() {
        return mIdCV;
    }

    public void setmIdCV(int mIdCV) {
        this.mIdCV = mIdCV;
    }

    public String getMtenCV() {
        return mtenCV;
    }

    public void setMtenCV(String mtenCV) {
        this.mtenCV = mtenCV;
    }
}
```

## 1.3. MainActivity.java <a id="1.3"></a>
________________________________________________________________________________________________________________________
- class MainActivity.java chứa những xử lý sự kiện
  - khởi tạo đối tượng Database
  - khởi tạo table của đối tượng Database
  - các phương thức thao tác đến Database
    - insert
    - update
    - delete
    - lấy đối tượng Cursor trỏ đến table
    - lấy data thông qua đối tượng Cursor
    - Refesh ListView mỗi khi có sự thay đổi ở Database

```java
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
```

## 1.4. CongViecAdapter.java <a id="1.4"></a>
________________________________________________________________________________________________________________________
```java
package com.hienqp.appghichu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CongViecAdapter extends BaseAdapter {
    private MainActivity mainActivity;
    private int dongCongViec;
    private List<CongViec> congViecList;

    // Constructor
    public CongViecAdapter(MainActivity mainActivity, int dongCongViec, List<CongViec> congViecList) {
        this.mainActivity = mainActivity;
        this.dongCongViec = dongCongViec;
        this.congViecList = congViecList;
    }

    @Override
    public int getCount() {
        return congViecList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView textViewTen;
        ImageView imageViewDelete, imageViewEdit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(dongCongViec, null);
            holder.textViewTen = (TextView) convertView.findViewById(R.id.textView_ten);
            holder.imageViewDelete = (ImageView) convertView.findViewById(R.id.imageView_delete);
            holder.imageViewEdit = (ImageView) convertView.findViewById(R.id.imageView_edit);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CongViec congViec = congViecList.get(position);

        holder.textViewTen.setText(congViec.getmTenCV());

        // bắt sự kiện 2 ImageView của ViewHolder
        holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.DialogUpdateCongViec(congViec.getmIdCV(), congViec.getmTenCV());
            }
        });

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.DialogDeleteCongViec(congViec.getmIdCV(), congViec.getmTenCV());
            }
        });

        return convertView;
    }

}
```

## 1.5. Các File Layout <a id="1.5"></a>
________________________________________________________________________________________________________________________
### 1.5.1. activity_main.xml <a id="1.5.1"></a>
________________________________________________________________________________________________________________________
- UI màn hình Main
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ListView
        android:id="@+id/listView_cong_viec"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:layout_marginBottom="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

### 1.5.2. layout_dialog_add_cong_viec.xml <a id="1.5.2"></a>
________________________________________________________________________________________________________________________
- UI dialog chức năng insert công việc khi nhấn vào menu add công việc
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#00CD4B">

    <TextView
        android:id="@+id/textView_add_cong_viec"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:padding="10dp"
        android:text="Thêm Công Việc"
        android:textColor="#FF0606"
        android:textSize="30sp"
        android:textStyle="bold" />

    <EditText
        android:id="@+id/editText_add_cong_viec"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_below="@+id/textView_add_cong_viec"
        android:layout_marginTop="10dp"
        android:hint="Nhập tên Công việc mới"
        android:padding="10dp" />

    <Button
        android:id="@+id/button_add_cong_viec"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/editText_add_cong_viec"
        android:layout_alignParentStart="true"
        android:layout_marginStart="30dp"
        android:layout_marginTop="20dp"
        android:text="Thêm" />

    <Button
        android:id="@+id/button_cancel_add_cong_viec"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/editText_add_cong_viec"
        android:layout_alignParentEnd="true"
        android:layout_marginTop="20dp"
        android:layout_marginEnd="30dp"
        android:text="Hủy" />
</RelativeLayout>
```

### 1.5.3. layout_dialog_update_cong_viec.xml <a id="1.5.3"></a>
________________________________________________________________________________________________________________________
- UI dialog chức năng update công việc khi nhấn vào Button edit trên ListView
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="10dp"
    android:background="#E4E2E2">

    <TextView
        android:id="@+id/textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:text="Thay đổi tên công việc"
        android:textSize="24sp"
        android:textStyle="bold" />

    <EditText
        android:id="@+id/dialog_editText_update_ten_cong_viec"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:hint="Nhập tên công việc mới" />

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp">

        <Button
            android:id="@+id/dialog_button_confirm_update_ten_cong_viec"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentStart="true"
            android:layout_marginEnd="20dp"
            android:text="Xác Nhận" />

        <Button
            android:id="@+id/dialog_button_cancel_update_ten_cong_viec"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_toEndOf="@id/dialog_button_confirm_update_ten_cong_viec"
            android:layout_alignParentEnd="true"
            android:layout_marginStart="20dp"
            android:text="Hủy" />
    </RelativeLayout>
</LinearLayout>
```

### 1.5.4. layout_dong_cong_viec.xml <a id="1.5.4"></a>
________________________________________________________________________________________________________________________
- UI hiển thị 1 dòng (View) trên ListView danh sách các công việc
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center_vertical"
    android:orientation="horizontal"
    android:padding="10dp"
    android:weightSum="10">

    <TextView
        android:layout_weight="8"
        android:id="@+id/textView_ten"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Tên Công Việc"
        android:textColor="#040112"
        android:textSize="24sp" />

    <ImageView
        android:layout_weight="1"
        android:id="@+id/imageView_delete"
        android:layout_width="0dp"
        android:layout_height="24dp"
        android:src="@drawable/delete48" />

    <ImageView
        android:layout_weight="1"
        android:id="@+id/imageView_edit"
        android:layout_width="0dp"
        android:layout_height="24dp"
        android:src="@drawable/edit48" />

</LinearLayout>
```

### 1.5.5. layout_menu_add_cong_viec.xml <a id="1.5.5"></a>
________________________________________________________________________________________________________________________
- UI hiển thị Menu chức năng insert công việc mới
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/menu_button_add"
        android:icon="@drawable/add48"
        android:title="Thêm"
        app:showAsAction="always" />
</menu>
```