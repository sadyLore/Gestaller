package com.example.gestaller.ui.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Client;
import com.example.gestaller.data.repository.ClientRepository;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {

    private List<Client> clientList;
    private final ClientRepository repository;

    public ClientAdapter(List<Client> clientList, ClientRepository repository) {
        this.clientList = clientList;
        this.repository = repository;
    }

    public void updateData(List<Client> clients) {
        this.clientList = clients;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Client client = clientList.get(position);
        holder.tvName.setText(client.getName());
        holder.tvPhone.setText(client.getPhone());
        holder.tvAddress.setText(client.getAddress());

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnOptions);
            popup.inflate(R.menu.menu_item_options);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    showEditDialog(v, client);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    repository.delete(client);
                    Toast.makeText(v.getContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return clientList != null ? clientList.size() : 0;
    }

    // 🔹 Mostrar diálogo de edición
    private void showEditDialog(View view, Client client) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_edit_client, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText etName = dialogView.findViewById(R.id.etEditName);
        EditText etPhone = dialogView.findViewById(R.id.etEditPhone);
        EditText etAddress = dialogView.findViewById(R.id.etEditAddress);

        etName.setText(client.getName());
        etPhone.setText(client.getPhone());
        etAddress.setText(client.getAddress());

        dialogView.findViewById(R.id.btnSaveEdit).setOnClickListener(v -> {
            client.setName(etName.getText().toString());
            client.setPhone(etPhone.getText().toString());
            client.setAddress(etAddress.getText().toString());
            repository.update(client);
            Toast.makeText(view.getContext(), "Cliente actualizado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btnCancelEdit).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAddress;
        ImageView btnOptions;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnOptions = itemView.findViewById(R.id.btnOptions);

        }
    }
}
