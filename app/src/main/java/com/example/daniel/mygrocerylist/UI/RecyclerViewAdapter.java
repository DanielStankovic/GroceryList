package com.example.daniel.mygrocerylist.UI;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.daniel.mygrocerylist.Activities.DetailsActivity;
import com.example.daniel.mygrocerylist.Data.DatabaseHandler;
import com.example.daniel.mygrocerylist.Model.Grocery;
import com.example.daniel.mygrocerylist.R;

import java.util.List;

/**
 * Created by Daniel on 3/4/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Grocery> groceryItems;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;


    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {

        this.context = context;
        this.groceryItems = groceryItems;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        Grocery grocery = groceryItems.get(position);

        holder.groceryItemName.setText(grocery.getName());
        holder.groceryItemQuantity.setText(grocery.getQuantity());
        holder.groceryItemDate.setText(grocery.getDateItemAdded());


    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView groceryItemName;
        public TextView groceryItemQuantity;
        public TextView groceryItemDate;
        public Button editButton;
        public Button deleteButton;
        public int id;



        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            groceryItemName = itemView.findViewById(R.id.name);
            groceryItemQuantity = itemView.findViewById(R.id.quantity);
            groceryItemDate = itemView.findViewById(R.id.dateAdded);

            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to next screen
                    int position = getAdapterPosition();

                    Grocery grocery = groceryItems.get(position);
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("name", grocery.getName());
                    intent.putExtra("quantity", grocery.getQuantity());
                    intent.putExtra("date", grocery.getDateItemAdded());
                    intent.putExtra("id", grocery.getId());
                    context.startActivity(intent);
                }
            });

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.editButton:

                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    editItem(grocery);
                    break;

                case R.id.deleteButton:

                     position = getAdapterPosition();
                     grocery = groceryItems.get(position);
                    deleteItem(grocery.getId());
                    break;


            }
        }


    public void deleteItem(final int id){

        builder = new AlertDialog.Builder(context);
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.confirmation_dialog, null);
        Button noButton = view.findViewById(R.id.noButton);
        Button yesButton = view.findViewById(R.id.yesButton);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db =  new DatabaseHandler(context);
                db.deleteGrocery(id);

                groceryItems.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());

                dialog.dismiss();
            }
        });
    }

    public void editItem(final Grocery grocery){

        builder = new AlertDialog.Builder(context);
        inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.popup, null);

        final EditText groceryItem = view.findViewById(R.id.grocery_item);
        final EditText quantity = view.findViewById(R.id.groceryQty);
        final TextView title = view.findViewById(R.id.title);
        title.setText("Edit Grocery");
        Button saveButton = view.findViewById(R.id.saveBtn);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(context);

                grocery.setName(groceryItem.getText().toString());
                grocery.setQuantity(quantity.getText().toString());

                if(!groceryItem.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty()){

                    db.updateGrocery(grocery);

                   notifyItemChanged(getAdapterPosition(), grocery);
                }else {
                    Snackbar.make(view, "Add Grocery and Quantity", Snackbar.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
}
}
