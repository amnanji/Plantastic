import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.databinding.FragmentCalendarBinding
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.ui.calendar.calendarAdapter
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendarView: CalendarView
    private lateinit var groupsRepository: GroupsRepository
    private lateinit var usersAuthRepository: UsersAuthRepository
    private lateinit var calendarAdapter: calendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root
        calendarView = binding.calendarView
        usersAuthRepository = UsersAuthRepository()
        val currUser = usersAuthRepository.getCurrentUser()
        groupsRepository = GroupsRepository()
        calendarAdapter = calendarAdapter(emptyList())

        binding.calendarRecyclerView.adapter = calendarAdapter
        binding.calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // REFERENCED: https://developer.android.com/reference/android/widget/CalendarView.OnDateChangeListener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDay = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            Log.d("Revs", "selectedDay $selectedDay")

            // Make sure to handle the null case appropriately
//            groupsRepository.getCalendarForUserAndDate(selectedDay.toString()) { calendarElements ->
//                // Update the adapter with the new data
//                calendarAdapter.updateCalendarElements(calendarElements)
//            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
