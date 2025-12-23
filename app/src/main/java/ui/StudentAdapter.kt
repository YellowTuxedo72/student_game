package ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.student_game.R
import model.Student

class StudentAdapter(
    private val students: List<Student>
) : RecyclerView.Adapter<StudentAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val record: TextView = view.findViewById(R.id.colRecord)
        val name: TextView = view.findViewById(R.id.colName)
        val course: TextView = view.findViewById(R.id.colCourse)
        val faculty: TextView = view.findViewById(R.id.colFaculty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_row, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val s = students[position]
        holder.record.text = s.recordBook
        holder.name.text = s.name
        holder.course.text = s.course
        holder.faculty.text = s.faculty
    }

    override fun getItemCount(): Int = students.size
}
