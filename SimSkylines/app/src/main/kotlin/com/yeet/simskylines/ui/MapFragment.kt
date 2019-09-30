package com.yeet.simskylines.ui

import android.Manifest
import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yeet.simskylines.R
import com.yeet.simskylines.model.*
import com.yeet.simskylines.util.ThreadExecutor
import com.yeet.simskylines.util.UserInterface
import java.io.File

/**
 * This Fragment represents the map recyclerview containing
 * all of the mapElements.
 */
class MapFragment : Fragment() {
    var selectorFragment: SelectorFragment? = null
    var informationFragment: InformationFragment? = null

    private var adapterPos = 0
    private val TAG = MapFragment::class.java.name
    private val REQUEST_THUMBNAIL = 1
    private val PERMISSION_REQUEST_CODE = 3

    private var photoFile: File? = null
    private var mapElement: MapElement? = null
    private var popup: PopupWindow? = null
    private var gameData: GameData? = null
    private var mapAdapter: MapAdapter? = null
    private var settings: Settings = Settings.getInstance()

    companion object {
        /**
         * Rotate the bitmap by a given number of degrees
         * @param bitmap
         * @param degree
         * @return rotated bitmap
         */
        fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
            val w = bitmap.width
            val h = bitmap.height
            val mtx = Matrix().apply {
                setRotate(degree.toFloat())
            }

            return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
        }
    }

    /**
     * Get GameData instance when the fragment is created initially
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameData = GameData.getInstance()
    }

    /**
     * Setup RecyclerView and adapter, and return the root view
     */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.mapAdapter = MapAdapter()
        val view = inflater.inflate(R.layout.map_recycler, container, false)
        view.findViewById<RecyclerView>(R.id.map_recyclerview).apply {
            layoutManager = (
                    GridLayoutManager(
                        activity,
                        settings.get(Settings.MAP_HEIGHT),
                        GridLayoutManager.HORIZONTAL,
                        false
                        )
                    )
            adapter = mapAdapter
        }

        return view
    }

    /**
     * Only actually used for the camera activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_THUMBNAIL) {
            val photo = BitmapFactory.decodeFile(photoFile.toString())
            mapElement?.image = rotateBitmap(photo, 90)
            mapAdapter?.notifyItemChanged(adapterPos)
        }
    }

    /**
     * When the camera activity launches, the popup has to be dismissed
     * or android complains about a leaked window
     */
    override fun onPause() {
        super.onPause()
        popup?.dismiss()
    }

    /**
     * MapAdapter inner class, handles creating viewholders
     */
    private inner class MapAdapter : RecyclerView.Adapter<MapViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
            return MapViewHolder(LayoutInflater.from(activity), parent)
        }

        override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
            val row = position % settings.get(Settings.MAP_HEIGHT)
            val col = position / settings.get(Settings.MAP_HEIGHT)
            holder.bind(gameData!![row, col], row, col)
        }

        override fun getItemCount(): Int {
            return settings.get(Settings.MAP_HEIGHT) * settings.get(Settings.MAP_WIDTH)
        }
    }

    /**
     * ViewHolder class
     */
    private inner class MapViewHolder(li: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(li.inflate(R.layout.map_grid, parent, false)) {
        private var currentMapElement: MapElement? = null
        private var currentRow = 0
        private var currentCol = 0

        private val northEast: ImageView
        private val northWest: ImageView
        private val southEast: ImageView
        private val southWest: ImageView
        private val structure: ImageView

        /**
         * Setup all the views required for this viewholder
         * and setup on click listeners as required
         */
        init {
            northEast = itemView.findViewById(R.id.imageNE)
            northWest = itemView.findViewById(R.id.imageNW)
            southEast = itemView.findViewById(R.id.imageSE)
            southWest = itemView.findViewById(R.id.imageSW)
            structure = itemView.findViewById(R.id.structure)

            // + 3 to prevent weird annoying rendering issues
            val size = parent.measuredHeight / settings.get(Settings.MAP_HEIGHT) + 2
            itemView.layoutParams.apply {
                width = size
                height = size
            }

            setupOnClick()
        }

        /**
         * bind each mapElement to viewholder, set image or structure as required
         * @param mapElement
         * @param row
         * @param col
         */
        fun bind(mapElement: MapElement, row: Int, col: Int) {
            this.currentMapElement = mapElement
            this.currentRow = row
            this.currentCol = col

            this.northEast.setImageResource(mapElement.northEast)
            this.northWest.setImageResource(mapElement.northWest)
            this.southWest.setImageResource(mapElement.southWest)
            this.southEast.setImageResource(mapElement.southEast)

            if(mapElement.image == null) {
                if(mapElement.structure == null) {
                    structure.setImageResource(android.R.color.transparent)
                } else {
                    structure.setImageResource(mapElement.structure!!.drawableId)
                }
            } else {
                structure.setImageBitmap(mapElement.image)
            }
        }

        /**
         * Check if the provided mapElement is buildable, if so, build the currently
         * selected structure, and let the GameData and InformationFragment classes know
         * so they can update their info
         * @see GameData
         * @see InformationFragment
         */
        fun checkBuildable(mapElement: MapElement) {
            var buildable = false
            /**
             * This looks like a clusterfuck, but really all its doing is making sure
             * at least of the adjacent sides has a Road on it so that building is allowed
             * I think theres a way to do this neater but ...
             */
            if(currentRow + 1 < settings.get(Settings.MAP_HEIGHT) && currentRow - 1 >= 0) {
                buildable = gameData!![currentRow + 1, currentCol].structure is Road ||
                        gameData!![currentRow - 1, currentCol].structure is Road
            }
            if(currentCol + 1 < settings.get(Settings.MAP_WIDTH) && currentCol - 1 >= 0) {
                buildable = buildable || gameData!![currentRow, currentCol - 1].structure is Road ||
                        gameData!![currentRow, currentCol + 1].structure is Road
            }

            // should be able to build roads anyway
            if(selectorFragment?.selectedStructure !is Road) {
                if (buildable) {
                    if(mapElement.isBuildable
                        && mapElement.structure == null) {
                        if(gameData!!.checkMoneyToast(this@MapFragment.context!!,
                                -1.0 * settings.getCost(selectorFragment?.selectedStructure!!).toDouble())) {
                            mapElement.structure = selectorFragment?.selectedStructure

                            when (mapElement.structure) {
                                is Residential -> gameData?.buildResidential()
                                is Commercial  -> gameData?.buildCommercial()
                            }
                        }
                    }
                } else if(mapElement.structure == null) {
                    UserInterface.showToast(
                        this@MapFragment.context!!,
                        "Must build adjacent to Road"
                    )
                }
            } else {
                if (mapElement.isBuildable
                    && mapElement.structure == null
                    && gameData!!.checkMoney(this@MapFragment.context!!)) {
                        mapElement.structure = selectorFragment!!.selectedStructure
                        gameData?.buildRoad()
                    }
            }

            /**
             * Refresh all relevant parts
             */
            mapAdapter?.notifyItemChanged(adapterPosition)
            informationFragment?.refresh()
            gameData?.refreshData(context!!)
        }

        /**
         * Check if it's okay to demolish
         * Update information with GameData
         * @param mapElement
         * @see GameData
         */
        fun checkDemolish(mapElement: MapElement) {
            if(informationFragment?.demolishSet!!) {
                when(mapElement.structure) {
                    is Residential -> gameData!!.nResidential--
                    is Commercial  -> gameData!!.nCommercial--
                }

                mapElement.image = null
                mapElement.structure = null
                gameData?.refreshData(context!!)
                informationFragment?.refresh()
                mapAdapter?.notifyItemChanged(adapterPosition)
            }
        }

        /**
         * Details Popup Window
         * Show the details for the provided mapElement
         * Constructs a popup view used to show the details for the clicked mapElement
         * If it contains a structure
         * @param mapElement
         */
        fun details(mapElement: MapElement) {
            if(mapElement.structure != null) {
                val inflater = context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.popup_layout, null)
                val et: EditText = view.findViewById(R.id.popup_text)
                val btn: Button = view.findViewById(R.id.popup_button)
                val tv: TextView = view.findViewById(R.id.coordinates_view)
                val type: TextView = view.findViewById(R.id.type_view)
                val image: ImageView = view.findViewById(R.id.popup_image)
                val popup = PopupWindow(view, ConstraintLayout.LayoutParams.MATCH_PARENT,
                                              ConstraintLayout.LayoutParams.MATCH_PARENT,
                                              true)

                // Set the image if available, or the structure if not
                if(mapElement.image == null) {
                    image.setImageResource(mapElement.structure?.drawableId!!)
                } else {
                    image.setImageBitmap(mapElement.image)
                }

                popup.animationStyle = R.style.popup_window_animation

                if(mapElement.owner != null) et.text = SpannableStringBuilder(mapElement.owner)
                else et.text = SpannableStringBuilder(mapElement.structure?.label)

                tv.text = getString(R.string.coordinates, currentRow, currentCol)
                type.text = getString(R.string.type, when(mapElement.structure) {
                    is Road        -> "Road"
                    is Commercial  -> "Commercial"
                    is Residential -> "Residential"
                    else           -> "This should never show"
                })

                image.setOnClickListener {
                    startCameraActivity()
                }

                btn.setOnClickListener {
                    popup.dismiss()
                }

                popup.setOnDismissListener {
                    mapElement.owner = SpannableStringBuilder(et.text).toString()
                }

                // Set some class fields for the MapFragment
                this@MapFragment.adapterPos = adapterPosition
                this@MapFragment.mapElement = currentMapElement
                this@MapFragment.popup = popup
                popup.showAtLocation(view.findViewById(R.id.popup_layout), Gravity.CENTER, 0, 0)
            }
        }

        /**
         * Function to setup and start the camera activity
         * also requests permission if permission not already given
         */
        private fun startCameraActivity() {
            /**
             * Starting the camera activity is done slightly differently here
             * Closer to how the API docs do it
             */
            val imagePath = File(this@MapFragment.activity?.applicationContext?.filesDir, "images")
            val imageFile = File(imagePath, "thumbnail.jpg")

            if(!imagePath.exists()) imagePath.mkdirs()
            if(!imageFile.exists()) imageFile.createNewFile()

            this@MapFragment.photoFile = imageFile

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val camUri = FileProvider.getUriForFile(this@MapFragment.activity as MapActivity,
                "com.yeet.simskylines.fileprovider", imageFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camUri)
            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            /**
             * This just asks for permission if it hasn't already been given
             */
            if(ContextCompat.checkSelfPermission(this@MapFragment.context!!,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MapFragment.activity!!,
                    arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
            } else {
                startActivityForResult(intent, REQUEST_THUMBNAIL)
            }
        }

        /**
         * Handles on click events, called for each on click listener
         */
        fun onclick() {
            if(informationFragment?.demolishSet!!) {
                currentMapElement?.let { checkDemolish(it) }
            } else {
                currentMapElement?.let {
                    details(it)
                    checkBuildable(it)
                }
            }

            ThreadExecutor.exec {
                currentMapElement?.let { gameData?.save(it, currentRow, currentCol) }
                gameData?.saveInformation()
            }
        }

        /**
         * Sets up on click for the views
         */
        fun setupOnClick() {
            northEast.setOnClickListener {
                onclick()
            }

            southEast.setOnClickListener {
                onclick()
            }

            northWest.setOnClickListener {
                onclick()
            }

            southWest.setOnClickListener {
                onclick()
            }
        }
    }
}