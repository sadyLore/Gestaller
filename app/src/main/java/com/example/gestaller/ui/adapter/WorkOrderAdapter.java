package com.example.gestaller.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.WorkOrder;
import com.example.gestaller.data.repository.WorkOrderRepository;
import com.example.gestaller.ui.AddWorkOrderActivity;
import com.example.gestaller.ui.WorkOrderDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder> {

    private List<WorkOrder> workOrderList;
    private final WorkOrderRepository repository;

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
        WorkOrder order = workOrderList.get(position);

        // 🧾 Cliente o título
        holder.tvTitle.setText(
                order.getClientName() != null && !order.getClientName().isEmpty()
                        ? order.getClientName()
                        : "Cliente desconocido"
        );

        // 🚗 Vehículo
        holder.tvClientVehicle.setText(
                (order.getVehicleBrand() != null ? order.getVehicleBrand() : "") + " " +
                        (order.getVehicleModel() != null ? order.getVehicleModel() : "") +
                        (order.getVehiclePlate() != null ? " - " + order.getVehiclePlate() : "")
        );

        // 📅 Fecha
        Date date = new Date(order.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvDate.setText("Fecha: " + sdf.format(date));

        // 👁️ Ver detalles
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WorkOrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            v.getContext().startActivity(intent);
        });

        // ⋮ Menú de opciones (editar o eliminar)
        holder.btnMoreOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnMoreOptions);
            popup.getMenuInflater().inflate(R.menu.menu_item_options, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    Intent intent = new Intent(v.getContext(), AddWorkOrderActivity.class);
                    intent.putExtra("workOrderId", order.getId());
                    v.getContext().startActivity(intent);
                    return true;
                } else if (id == R.id.action_delete) {
                    repository.delete(order);
                    Toast.makeText(v.getContext(), "Trabajo eliminado", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return workOrderList != null ? workOrderList.size() : 0;
    }

    public void updateData(List<WorkOrder> newList) {
        this.workOrderList = newList;
        notifyDataSetChanged();
    }

    static class WorkOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvClientVehicle, tvDate;
        ImageView btnMoreOptions;

        public WorkOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvClientVehicle = itemView.findViewById(R.id.tvClientVehicle);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
        }
    }
}
