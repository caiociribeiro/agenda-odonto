import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.agendaodonto.fragments.ConsultasHistoricoFragment
import com.example.agendaodonto.fragments.ConsultasPendenteFragment

class DentistaHomeTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ConsultasPendenteFragment()
            1 -> ConsultasHistoricoFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}