package com.example.gestaller.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaller.R;
import com.example.gestaller.data.local.entity.Vehicle;
import com.example.gestaller.data.repository.VehicleRepository;

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

        holder.itemView.setOnLongClickListener(v -> {
            repository.delete(vehicle);
            Toast.makeText(v.getContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList != null ? vehicleList.size() : 0;
    }

    // 🔹 Método para actualizar lista desde LiveData
    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicleList = vehicles;
        notifyDataSetChanged();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandModel;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandModel = itemView.findViewById(R.id.tvBrandModel);
        }
    }
}
