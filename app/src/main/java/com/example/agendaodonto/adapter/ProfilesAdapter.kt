import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.Doctor
import com.example.agendaodonto.DoctorProfileActivity
import com.example.agendaodonto.R

class ProfilesAdapter(private val doctorList: List<Doctor>) :
    RecyclerView.Adapter<ProfilesAdapter.DoctorViewHolder>() {

    // ViewHolder para armazenar as views do card
    class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorName: TextView = view.findViewById(R.id.tv_doctor_name)
        val doctorSpecialty: TextView = view.findViewById(R.id.tv_specialty)
        val doctorRating: TextView = view.findViewById(R.id.tv_doctor_rating)
        val doctorAvatar: ImageView = view.findViewById(R.id.iv_doctor_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]

        // Definir os dados do médico no card
        holder.doctorName.text = doctor.name
        holder.doctorSpecialty.text = doctor.specialty
        holder.doctorRating.text = doctor.rating.toString()

        // Definir o avatar (pode ser uma imagem padrão ou carregada dinamicamente)
        holder.doctorAvatar.setImageResource(R.drawable.ic_dentist)

        // Ao clicar no card, abrir a DoctorProfileActivity e passar os dados do médico
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DoctorProfileActivity::class.java)

            // Passar os dados do médico para a DoctorProfileActivity
            intent.putExtra("doctorName", doctor.name)
            intent.putExtra("doctorSpecialty", doctor.specialty)
            intent.putExtra("doctorRating", doctor.rating)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return doctorList.size
    }
}
