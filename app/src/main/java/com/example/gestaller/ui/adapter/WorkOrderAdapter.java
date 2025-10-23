package com.example.gestaller.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder> {

    private List<WorkOrder> workOrderList;
    private WorkOrderRepository repository;

    public WorkOrderAdapter(List<WorkOrder> workOrderList, WorkOrderRepository repository) {
        this.workOrderList = workOrderList;
        this.repository = repository;
    }

    @NonNull
    @Override
    public WorkOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workorder, parent, false);
        return new WorkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkOrderViewHolder holder, int position) {
        WorkOrder workOrder = workOrderList.get(position);

        holder.tvTitle.setText("Trabajo #" + workOrder.getId());
        holder.tvClientVehicle.setText("Vehículo ID: " + workOrder.getVehicleId());
        holder.tvPrice.setText("Total: Gs. " + String.format("%,.0f", workOrder.getTotalPrice()));

        // Mostrar fecha (opcional)
        Date date = new Date(workOrder.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvDate.setText("Fecha: " + sdf.format(date));

        holder.itemView.setOnLongClickListener(v -> {
            repository.delete(workOrder);
            Toast.makeText(v.getContext(), "Trabajo eliminado", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return workOrderList != null ? workOrderList.size() : 0;
    }

    static class WorkOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvClientVehicle, tvPrice, tvDate;

        public WorkOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvClientVehicle = itemView.findViewById(R.id.tvClientVehicle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
