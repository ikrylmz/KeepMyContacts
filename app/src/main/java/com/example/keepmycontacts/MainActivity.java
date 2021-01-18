package com.example.keepmycontacts;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    EditText et_key;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_key = (EditText)findViewById(R.id.key_et);
        /*
        list = findViewById(R.id.list);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, conctactList);
        list.setAdapter(adapter);

         */
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.get_contact_btn:

                Intent i = new Intent(this,SelectionContactActivity.class);
                startActivity(i);
            break;
            case R.id.download_btn:
                //sendEmail();
                //addContact("5695555","İlker");
                downloadContacts();
                //upload();
                /*
                GMailSender gMailSender = new GMailSender();
                gMailSender.send("contactskeep11@gmail.com",
                        "akLmhjg.9",
                        "ilkeryilmaz9706@gmail.com",
                        "kşjbads",
                        "merhaba");
                /*
                for(int i = 0;i<conctactList.size();i++) {
                    upload("Concacts", conctactList.get(i));
                }

                   /*
                Conctact conctact = new Conctact();
                conctact.setPhoneNumber("0546543123");
                conctact.setName("İlker YILMAZ");

                Conctact conctact2 = new Conctact();
                conctact2.setPhoneNumber("6546531");
                conctact2.setName("Cenap Akşahin");

                ArrayList<Conctact> conctacts = new ArrayList<>();
                conctacts.add(conctact);
                conctacts.add(conctact2);


                firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                String key = databaseReference.push().getKey();

                for(int i = 0;i<conctacts.size();i++) {
                    upload("Concacts/"+key, conctacts.get(i));
                }
            */
                break;
        }
    }

    private void addContact(String number,String name) {

        ArrayList<ContentProviderOperation> op_list = new ArrayList<>();
        op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                //.withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT)
                .build());
        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "")
                .build());

        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, " ")
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        try {
            ContentProviderResult[] results = getContentResolver().
                    applyBatch(ContactsContract.AUTHORITY, op_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadContacts()
    {
        //Log.i("downloadContacts : ", key );
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(et_key.getText().toString());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                   Conctact contact = ds.getValue(Conctact.class);
                   Log.i("Contact Downloaded",contact.getName() + " " + contact.getPhoneNumber());
                   addContact(contact.getPhoneNumber(),contact.getName());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Contact downloading",databaseError.getMessage());
            }
        });
    }
    public void upload()
    {
        Conctact conctact = new Conctact();
        conctact.setName("Mehmer");
        conctact.setPhoneNumber("655445654653436512");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //  DatabaseReference databaseReference2 = firebaseDatabase.getReference();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("1s").child("Contact2").setValue(conctact);

    }
 }


































