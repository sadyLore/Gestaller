package com.example.gestaller.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.repository.ServiceTemplateRepository;
import com.example.gestaller.ui.AddServiceTemplateActivity;

import java.util.List;

public class ServiceTemplateAdapter extends RecyclerView.Adapter<ServiceTemplateAdapter.ServiceViewHolder> {

    private List<ServiceTemplate> serviceList;
    private ServiceTemplateRepository repository;

    public ServiceTemplateAdapter(List<ServiceTemplate> serviceList, ServiceTemplateRepository repository) {
        this.serviceList = serviceList;
        this.repository = repository;
    }

    public void updateData(List<ServiceTemplate> newList) {
        this.serviceList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceTemplate service = serviceList.get(position);

        holder.tvName.setText(service.getName());
        holder.tvDescription.setText(service.getDescription());
        holder.tvPrice.setText("Gs. " + String.format("%,.0f", service.getDefaultPrice()));

        // 🔹 Botón de opciones (⋮)
        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnOptions);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_item_options, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    Intent intent = new Intent(v.getContext(), AddServiceTemplateActivity.class);
                    intent.putExtra("serviceId", service.getId());
                    intent.putExtra("name", service.getName());
                    intent.putExtra("description", service.getDescription());
                    intent.putExtra("price", service.getDefaultPrice());
                    v.getContext().startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    repository.delete(service);
                    Toast.makeText(v.getContext(), "Servicio eliminado", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice;
        ImageView btnOptions;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnOptions = itemView.findViewById(R.id.btnOptions);
        }
    }
}
