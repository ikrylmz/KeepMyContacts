package com.example.keepmycontacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class SelectionContactActivity extends AppCompatActivity {

    public static final int REQUEST_READ_CONTACTS = 79;
    ArrayList<Conctact> contactListUploaded = new ArrayList<>();
    ArrayList<Conctact> contactList = new ArrayList<>();
    RecyclerView rv_contacs;
    Button btn_upload,btn_choose_all;
    FirebaseDatabase firebaseDatabase;
    private String key;
    EditText et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_contact);

        et_email = findViewById(R.id.email_et);

        btn_upload = findViewById(R.id.upload_btn);
        btn_choose_all = findViewById(R.id.choose_all_btn);

        rv_contacs = (RecyclerView)findViewById(R.id.contacs_rv);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {

            contactList = sort(getAllContacts());
            Log.i("Contact List","Liste was sorted");
            setRecyclerView(contactList);

        } else {
            requestPermission();
        }
    }

    private ArrayList<Conctact> getAllContacts() {
        ArrayList<Conctact> conctactList2 = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                Conctact conctact = new Conctact();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                conctact.setName(name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        conctact.setPhoneNumber(phoneNo);

                    }
                    pCur.close();
                }
                conctactList2.add(conctact);
            }
        }
        if (cur != null) {
            cur.close();
        }
        Log.i("Contact List","List was received");
        return conctactList2;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ArrayList<Conctact> contactList = sort(getAllContacts());
                    setRecyclerView(contactList);

                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    private ArrayList<Conctact> sort(ArrayList<Conctact> arrayList)
    {
        SortingContacts sortingContacts = new SortingContacts();
        Conctact[] array = convertListToArray(arrayList);

        sortingContacts.sort(array,array.length);

        return convertArrayToList(array);
    }
    private Conctact[]  convertListToArray(ArrayList<Conctact> arrayList)
    {
        Conctact[] contactArray = new Conctact[arrayList.size()];
        for(int i = 0;i<arrayList.size();i++)
        {
            contactArray[i] = arrayList.get(i);
        }
        return contactArray;
    }
    private ArrayList<Conctact>  convertArrayToList(Conctact[] array)
    {
        ArrayList<Conctact> conctactList = new ArrayList<>();

        for(int i = 0;i<array.length;i++)
        {
            conctactList.add((array[i]));
        }
        return conctactList;
    }

    private void setRecyclerView(ArrayList<Conctact> conctactList)
    {
        RvContacts.CheckedListener listener = new RvContacts.CheckedListener() {
            @Override
            public void onItemChecked(Conctact conctactData) {
                Log.i("Contact in setRView : ","For Example Contact name : " +  conctactData.getName());
                contactListUploaded.add(conctactData);
            }
        };

        RvContacts rvContacts = new RvContacts(conctactList,listener,getApplicationContext());
        rv_contacs.setAdapter(rvContacts);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        rv_contacs.setLayoutManager(llm);
        btn_upload.setEnabled(true);
        btn_choose_all.setEnabled(true);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.choose_all_btn:

                for(int i = 0;i<rv_contacs.getChildCount();i++) {

                View view =  rv_contacs.findViewHolderForLayoutPosition(i).itemView;
                CheckBox checkbox = (CheckBox)view.findViewById(R.id.this_contact_cbox);
                checkbox.setChecked(true);
                }
                contactListUploaded = contactList;
                Log.i("List To Be Uploaded","List was given to contactListUploaded");
                break;
            case R.id.back_toMain_btn:
                startActivity( new Intent(this,MainActivity.class));
                break;
            case R.id.upload_btn:
                firebaseDatabase = FirebaseDatabase.getInstance();
                generateKey();
                contactListUploaded = removeRepeateds(contactListUploaded);
                for(int i = 0;i<contactListUploaded.size();i++) {
                    upload(i,contactListUploaded.get(i));
                }
                Log.i("Uploading List","List was uploaded");
                String  receiverMail = et_email.getText().toString();
                sendEmail(receiverMail);
                break;
        }
    }
    private ArrayList<Conctact> removeRepeateds(ArrayList<Conctact> list)
    {
        for(int i = 0;i<list.size();i++)
        {
           Conctact contact =  list.get(i);
           list.remove(i);
           list.contains(contact);
        }
        return list;
    }
    private void upload(int i, Conctact conctact) {

        if(conctact == null)
        {
            Log.e("Contact in upload","contact is null");
            return;
        }

        DatabaseReference databaseReference = firebaseDatabase.getReference().child(key).child("Contact"+i);
        databaseReference.setValue(conctact);
    }
    private void generateKey()
    {

        DatabaseReference databaseReference = firebaseDatabase.getReference();
        key = databaseReference.push().getKey();
        Log.i("Key","Key is generated : " + key);

    }

    protected void sendEmail(final String email) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    GMailSender gMailSender = new GMailSender("contactskeep11", "akLmhjg.9");

                    gMailSender.sendMail("Keep My Contacts", "Key : " + key,
                            "contactskeep11@gmail.com", email);
                    Log.e("SendMail", "mail sended");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }
}
