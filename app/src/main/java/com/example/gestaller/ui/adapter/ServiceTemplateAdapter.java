package com.example.gestaller.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.ServiceTemplate;
import com.example.gestaller.data.repository.ServiceTemplateRepository;

import java.util.List;

public class ServiceTemplateAdapter extends RecyclerView.Adapter<ServiceTemplateAdapter.ServiceViewHolder> {

    private List<ServiceTemplate> serviceList;
    private ServiceTemplateRepository repository;

    public ServiceTemplateAdapter(List<ServiceTemplate> serviceList, ServiceTemplateRepository repository) {
        this.serviceList = serviceList;
        this.repository = repository;
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

        // Old code
        // New, corrected code
        holder.tvName.setText(service.getName());
        holder.tvDescription.setText(service.getDescription());
        holder.tvPrice.setText("Gs. " + String.format("%,.0f", service.getDefaultPrice()));

        holder.itemView.setOnLongClickListener(v -> {
            repository.delete(service);
            Toast.makeText(v.getContext(), "Servicio eliminado", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
