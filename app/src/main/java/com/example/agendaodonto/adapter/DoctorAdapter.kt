import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.Doctor
import com.example.agendaodonto.R

class DoctorAdapter(private val doctorList: List<Doctor>) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    // ViewHolder para armazenar as views do card
    class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorName: TextView = view.findViewById(R.id.tv_doctor_name)
        val doctorSpecialty: TextView = view.findViewById(R.id.tv_specialty)
        val doctorRating: TextView = view.findViewById(R.id.tv_doctor_rating)
        val doctorAvatar: ImageView = view.findViewById(R.id.iv_doctor_avatar)
        val ratingStar: ImageView = view.findViewById(R.id.iv_star)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_doctor, parent, false) // Inflando o card
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.doctorName.text = doctor.name
        holder.doctorSpecialty.text = doctor.specialty
        holder.doctorRating.text = doctor.rating.toString()

        // Exemplo de avatar (se precisar carregar de uma URL, pode usar Glide ou Picasso)
        holder.doctorAvatar.setImageResource(R.drawable.avatar_background)

        // Clique para selecionar o médico e ir para a página de calendário (pode ser implementado depois)
        holder.itemView.setOnClickListener {
            // TODO: Navegar para a página de calendário
        }
    }

    override fun getItemCount(): Int {
        return doctorList.size
    }
}