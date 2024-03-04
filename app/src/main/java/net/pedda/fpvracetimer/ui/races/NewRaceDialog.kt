package net.pedda.fpvracetimer.ui.races

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import net.pedda.fpvracetimer.R
import net.pedda.fpvracetimer.db.DBUtils
import net.pedda.fpvracetimer.db.Race

class NewRaceDialog(private val mActivity: Activity, private val mTitle: String): DialogFragment() {

    var racename: String = "";

    var mBuilder: AlertDialog.Builder = AlertDialog.Builder(mActivity);

    var mContentView: View? = null;
    var mEditText: EditText? = null;

    private fun setupDialog(): Unit {

        val inflater: LayoutInflater = requireActivity().layoutInflater;

        mContentView = inflater.inflate(R.layout.dialog_newrace, null)

        mEditText = mContentView?.findViewById(R.id.dialog_race_newname)

        mBuilder.apply {
            setTitle(mTitle)
            setView(mContentView)
            setPositiveButton(R.string.btn_Create) { dialog, _ ->
                val r = Race()
                r.racename = mEditText?.text.toString()
                DBUtils.insertRace(r)
            }
            setNegativeButton(R.string.btn_Cancel, null)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setupDialog()
        return mBuilder.create()
    }
}