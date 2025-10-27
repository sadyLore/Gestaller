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
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.repository.VehicleRepository;
import com.example.gestaller.ui.AddVehicleActivity;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;
    private final VehicleRepository repository;

    public VehicleAdapter(List<Vehicle> vehicleList, VehicleRepository repository) {
        this.vehicleList = vehicleList;
        this.repository = repository;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.tvBrandModel.setText(vehicle.getBrand() + " " + vehicle.getModel());

        holder.btnMoreOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnMoreOptions);
            popup.getMenuInflater().inflate(R.menu.menu_item_options, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    // Editar vehículo
                    Intent intent = new Intent(v.getContext(), AddVehicleActivity.class);
                    intent.putExtra("vehicleId", vehicle.getId());
                    intent.putExtra("brand", vehicle.getBrand());
                    intent.putExtra("model", vehicle.getModel());
                    v.getContext().startActivity(intent);
                    return true;

                } else if (id == R.id.action_delete) {
                    // Eliminar vehículo
                    repository.delete(vehicle);
                    Toast.makeText(v.getContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList != null ? vehicleList.size() : 0;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicleList = vehicles;
        notifyDataSetChanged();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandModel;
        ImageView btnMoreOptions;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandModel = itemView.findViewById(R.id.tvBrandModel);
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
        }
    }
}
