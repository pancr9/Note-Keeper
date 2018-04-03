package builderspace.notekeeper.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import builderspace.notekeeper.R;
import builderspace.notekeeper.model.Note;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private final HashMap<Integer, String> priorityMap = new HashMap<Integer, String>() {
        {
            put(1, "High Priority");
            put(2, "Medium Priority");
            put(3, "Low Priority");
        }
    };
    private List<Note> noteList;
    private Context mContext;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mNoteRef = mRootRef.child("Notes");

    public RecyclerAdapter(Context context, List<Note> noteList) {
        this.noteList = noteList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Note note = noteList.get(position);
        PrettyTime p = new PrettyTime();

        holder.textViewNote.setText(note.getTaskNote());
        holder.textViewPriority.setText(priorityMap.get(note.getPriority()));
        holder.textViewTime.setText(p.format(note.getCreatedTime()));
        holder.cbStatus.setChecked(note.getStatus().equals("completed"));

        holder.cbStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("Do you really want to mark it is as pending??")
                        .setPositiveButton(R.string.yes, (dialog13, which) -> {

                            note.setStatus("pending");
                            note.setCreatedTime(Calendar.getInstance().getTime());
                            mNoteRef.child(note.getId()).setValue(note);
                        })
                        .setNegativeButton(R.string.no, (dialog12, which) -> holder.cbStatus.setChecked(true)).create();
                dialog.setOnShowListener(dialog1 -> {
                });
                dialog.show();

            } else {
                note.setStatus("completed");
                note.setCreatedTime(Calendar.getInstance().getTime());
                mNoteRef.child(note.getId()).setValue(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNote, textViewPriority, textViewTime;
        CheckBox cbStatus;

        MyViewHolder(View convertView) {
            super(convertView);
            textViewNote = convertView.findViewById(R.id.tvNoteText);
            textViewPriority = convertView.findViewById(R.id.tvPriority);
            cbStatus = convertView.findViewById(R.id.cbStatus);
            textViewTime = convertView.findViewById(R.id.tvUpdatedOn);

            convertView.setOnLongClickListener(v -> {
                int position = getLayoutPosition();
                Note note = noteList.get(position);
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("Do you really want to delete?")
                        .setPositiveButton(R.string.yes, (dialog13, which) -> mNoteRef.child(note.getId()).removeValue())
                        .setNegativeButton(R.string.no, (dialog12, which) -> {

                        }).create();
                dialog.setOnShowListener(dialog1 -> {
                });
                dialog.show();
                return true;
            });
        }
    }
}


