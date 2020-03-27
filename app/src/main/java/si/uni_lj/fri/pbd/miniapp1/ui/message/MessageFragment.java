package si.uni_lj.fri.pbd.miniapp1.ui.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import si.uni_lj.fri.pbd.miniapp1.MainActivity;
import si.uni_lj.fri.pbd.miniapp1.R;
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.Contact;

public class MessageFragment extends Fragment {

    private ArrayList<Contact> list;

    private final String subject = "subject";
    private final String message = "Sent from my MiniApp1";

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        final MainActivity activity = (MainActivity) getActivity();
        list = activity.getContactList();

        Button emailBtn = (Button) view.findViewById(R.id.btn_email);
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] emailArray = emailContacts();
                if (emailArray.length > 0) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, emailArray);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                    try {
                        startActivity(Intent.createChooser(emailIntent, "Choose an email client :"));
                    } catch (android.content.ActivityNotFoundException ex){
                        Toast.makeText(getContext(), "Uh...No email app?", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No email addresses found for selected contacts!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button mmsBtn = (Button) view.findViewById(R.id.btn_mms);
        mmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumbers = mmsContacts();
                if (phoneNumbers.length() > 0) {
                    Intent mmsIntent = new Intent(Intent.ACTION_VIEW);
                    mmsIntent.setData(Uri.parse("mmsto:" + phoneNumbers));
                    mmsIntent.putExtra("sms_body", message);
                    try {
                        startActivity(Intent.createChooser(mmsIntent, "Choose an mms client :"));
                    } catch (android.content.ActivityNotFoundException ex){
                        Toast.makeText(getContext(), "Uh...No mms app?", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No phone numbers found for selected contacts!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public String[] emailContacts() {
        int noEmail = 0;
        List<String> emailList = new ArrayList<String>();
        for (Contact c : list) {
            if (c.getContactState() == true) {
                if (c.getContactEmailAddress() != null) {
                    emailList.add(c.getContactEmailAddress());
                } else {
                    noEmail++;
                }
            }
        }

        String[] emails = new String[emailList.size()];
        int i = 0;
        for (String s : emailList) {
            emails[i] = s;
            i++;
        }

        if (noEmail > 0) {
            Toast.makeText(getContext(), noEmail + " out of " + emails.length + "don't have email address!", Toast.LENGTH_SHORT).show();
        }

        return emails;
    }

    // We are doing this beacuse String.join() cal requires API lvl 26
    // and my minimum requirement is set to 16
    public String mmsContacts() {
        int noPhoneNumber = 0;
        List<String> numberList = new ArrayList<String>();
        for (Contact c : list) {
            if (c.getContactState() == true) {
                if (c.getContactPhoneNumber() != null) {
                    numberList.add(c.getContactPhoneNumber());
                } else {
                    noPhoneNumber++;
                }
            }
        }

        String[] numbers = new String[numberList.size()];
        int i = 0;
        for (String s : numberList) {
            numbers[i] = s;
            i++;
        }

        if (noPhoneNumber > 0) {
            Toast.makeText(getContext(), noPhoneNumber + " out of " + numbers.length + "don't have phone number!", Toast.LENGTH_SHORT).show();
        }

        if (numbers.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < numbers.length - 1; j++) {
                sb.append(numbers[j]);
                sb.append(",");
            }
            sb.append(numbers[numbers.length-1]);
            return sb.toString();
        }

        return null;
    }
}
