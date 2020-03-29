/*
 * MESSAGE FRAGMENT
 *
 * This fragment is used to send messages via email or mms.
 *
 */

package si.uni_lj.fri.pbd.miniapp1.ui.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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

    // email/mms content
    private final String subject = "Hello there";
    private final String message = "Sent from my MiniApp1";

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        // gets contactList from MainActivity
        final MainActivity activity = (MainActivity) getActivity();
        list = activity.getContactList();
        // email button setup
        Button emailBtn = (Button) view.findViewById(R.id.btn_email);
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gets only email addresses
                String[] emailArray = emailContacts();
                if (emailArray.length > 0) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, emailArray);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    try {
                        startActivity(Intent.createChooser(intent, "Choose an email client :"));
                    } catch (android.content.ActivityNotFoundException ex){
                        Toast.makeText(getContext(), "Uh...No email app?", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // mms button setup
        Button mmsBtn = (Button) view.findViewById(R.id.btn_mms);
        mmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gets only phone numbers
                String phoneNumbers = mmsContacts();
                if (phoneNumbers != null) {
                    Intent mmsIntent = new Intent(Intent.ACTION_VIEW);
                    mmsIntent.setData(Uri.parse("mmsto:" + phoneNumbers));
                    mmsIntent.putExtra("sms_body", message);
                    try {
                        startActivity(Intent.createChooser(mmsIntent, "Choose a mms client :"));
                    } catch (android.content.ActivityNotFoundException ex){
                        Toast.makeText(getContext(), "Uh...No mms app?", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    // gets email addresses from ArrayList
    private String[] emailContacts() {
        // counts how many contacts doesn't have email address
        int noEmail = 0;
        // creates list of email addresses (because we don't know how many contacts have them)
        List<String> emailList = new ArrayList<String>();
        for (Contact c : list) {
            if (c.getContactState()) {
                if (c.getContactEmailAddress() != null) {
                    emailList.add(c.getContactEmailAddress());
                } else {
                    noEmail++;
                }
            }
        }
        // now we know number of contacts with email address so we put them in an array
        String[] emails = new String[emailList.size()];
        int i = 0;
        for (String s : emailList) {
            emails[i] = s;
            i++;
        }

        // toast messages just for fun
        if (emails.length == 0) {
            Toast.makeText(getContext(), "No email addresses found for selected contacts!", Toast.LENGTH_SHORT).show();
        } else if (noEmail > 0) {
            Toast.makeText(getContext(), noEmail + " out of " + (emails.length + noEmail) + " don't have email address!", Toast.LENGTH_SHORT).show();
        }

        return emails;
    }

    // gets phone numbers from ArrayList, method is analogue to emailContacts()
    // We are doing this because String.join() call requires API lvl 26
    // and my minimum requirement is set to 16.
    public String mmsContacts() {
        int noPhoneNumber = 0;
        List<String> numberList = new ArrayList<String>();
        for (Contact c : list) {
            if (c.getContactState()) {
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

        if (numbers.length == 0) {
            Toast.makeText(getContext(), "No phone numbers found for selected contacts!", Toast.LENGTH_SHORT).show();
        } else if (noPhoneNumber > 0) {
            Toast.makeText(getContext(), noPhoneNumber + " out of " + (numbers.length + noPhoneNumber) + " don't have phone number!", Toast.LENGTH_SHORT).show();
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
