package appstopper.mobile.cs.fsu.edu.appstopper;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private static final String PREFS_NAME = "DevicePrefsFile"; // Name of shared preferences file
    private static final String TAG = "DashboardFragment";
    private RecyclerView entriesView;
    private static RecyclerView.Adapter viewAdapter;
    private RecyclerView.LayoutManager viewManager;
    private static ArrayList<WhitelistEntry> entriesDataset = new ArrayList<>();
    private static EntryDao entryDao;
    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        entryDao = Room.databaseBuilder(getActivity().getApplicationContext(),
                AppDatabase.class, "Whitelist").build().entryDao();

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        SharedPreferences sPref = getContext().getSharedPreferences(PREFS_NAME, 0);
        String deviceID = sPref.getString("deviceID", "error");

        entriesView = (RecyclerView) root.findViewById(R.id.entries_view);
        viewManager = new LinearLayoutManager(root.getContext());
        viewAdapter = new WhitelistEntriesAdapter(entriesDataset, getActivity().getApplicationContext());

        entriesView.setLayoutManager(viewManager);
        entriesView.setAdapter(viewAdapter);

        new GetEntryTask().execute(null, null, null);

        // Populating entries with whiteList
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference whitelistRef = rootRef.child("devices")
                .child(deviceID).child("whitelist_entries");
        return root;
    }

    private static class GetEntryTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... voids) {
            List<WhitelistEntry> temp = entryDao.getAll();
            for (WhitelistEntry i : temp) {
                if (i != null) {
                    Log.v(TAG, "getEntry: " + i.packageName);
                    entriesDataset.add(i);
                }
            }
            viewAdapter.notifyDataSetChanged();
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(ArrayList<String> result) {
            viewAdapter.notifyDataSetChanged();
        }
    }
}
