package com.nas.polyplan

import android.app.Activity
import android.content.*
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.nas.polyplan.databinding.ProfileFragmentBinding
import io.getstream.avatarview.coil.loadImage
import java.util.*

class ProfileFragment : Fragment() {
    private var binding: ProfileFragmentBinding? = null
    private var inFamily: SharedPreferences? = null
    private var viewModel: ProfileViewModel? = null
    var s: String? = null
    private var familyId: SharedPreferences? = null
    private var d: String? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        inFamily = activity?.getSharedPreferences("inFamily", Context.MODE_PRIVATE)
        s = inFamily!!.getString("inFamily", "0")
        familyId = activity?.getSharedPreferences("familyId", Context.MODE_PRIVATE)
        d = familyId!!.getString("familyId", "")
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel!!.user.observe(viewLifecycleOwner) { user -> updateDataFromUser(user) }
        updateDataFromUser(viewModel!!.user.value)

        binding!!.id.setOnClickListener(View.OnClickListener {
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(binding!!.id.text, binding!!.id.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(activity, "ID was coppied to clipboard!", Toast.LENGTH_SHORT).show()
        })

        connectActionHandlers()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun updateDataFromUser(user: User?) {
        if (user != null) {
            binding!!.pfUserNameMsg.text = user.username
            binding!!.pfUserEmailMsg.text = user.email
            binding!!.id.text = user.family
            binding!!.role.text = user.role

            val nameParts = user.username.split("\"\\\\s+\"").toTypedArray()
            val nameInitials = StringBuilder()
            for (part in nameParts) {
                nameInitials.append(part[0])
            }
            binding!!.pfAvatarView.avatarInitials = nameInitials.toString().uppercase(Locale.getDefault())

            if (user.profilePicture.isNotEmpty()) {
                FirebaseStorage.getInstance()
                        .reference.child(user.profilePicture)
                        .downloadUrl.addOnSuccessListener {
                            binding!!.pfAvatarView.avatarInitials = ""
                            binding!!.pfAvatarView.loadImage(it)
                        }
            }

        }
    }


    private fun connectActionHandlers() {
        binding?.pfLogoutBtn!!.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            inFamily!!.edit().putString("inFamily", "0").apply()
            familyId!!.edit().putString("familyId", "").apply()
            startActivity(Intent(activity, Authorization::class.java))
            activity?.finish()
        }

        binding?.pfAvatarView!!.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK

            resultLauncher.launch(intent)
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result?.data?.data
            val inputStream = context?.contentResolver?.openInputStream(uri!!)

            try {
                val bitmap = BitmapFactory.decodeStream(inputStream)

                binding?.pfAvatarView?.avatarInitials = ""
                binding!!.pfAvatarView.loadImage(bitmap)

                val link = "images/" + UUID.randomUUID()

                FirebaseStorage.getInstance()
                        .reference.child(link)
                        .putFile(uri!!).addOnSuccessListener { snapshot ->
                            snapshot.storage.downloadUrl.addOnSuccessListener {
                                FirebaseDatabase.getInstance()
                                        .getReference("user/" + FirebaseAuth.getInstance().currentUser!!.uid + "/profilePicture")
                                        .setValue(link).addOnSuccessListener {
                                            Toast.makeText(
                                                    context,
                                                    "Uploading completed",
                                                    Toast.LENGTH_LONG
                                            ).show()
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                    context,
                                                    "Uploading failed",
                                                    Toast.LENGTH_LONG
                                            ).show()
                                        }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                    context,
                                    "Something went wrong",
                                    Toast.LENGTH_LONG
                            ).show()
                        }
            } finally {
                inputStream?.close()
            }
        }
    }

}